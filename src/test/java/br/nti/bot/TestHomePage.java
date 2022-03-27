package br.nti.bot;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import lombok.extern.java.Log;

/**
 * Simple test using the WicketTester
 */
@Log
public class TestHomePage {
	private WicketTester tester;

	@Before
	public void setUp() {
		tester = new WicketTester(new WicketApplication());
	}

	@Test
	public void homepageRendersSuccessfully() {
		// start and render the test page
		tester.startPage(HomePage.class);

		// assert rendered page class
		tester.assertRenderedPage(HomePage.class);
	}

	@Test
	public void unirestCall() {
		HttpResponse<String> responseAssets = Unirest.get("http://api.coincap.io/v2/assets").header("accept", "*/*")
				.header("content-type", "application/json; charset=utf-8").header("accept-encoding", "gzip, deflate")
				.header("Authorization", "Bearer eaa33432-5fff-4d6b-8e85-52e9897071e9")
				.asString();

		Assert.assertNotNull(responseAssets);

		JsonObject obj = new Gson().fromJson(responseAssets.getBody(), JsonObject.class);

		Assert.assertTrue(obj.isJsonObject());

		JsonArray assetsJsonArray = obj.get("data").getAsJsonArray();
		Assert.assertNotNull(assetsJsonArray);

		List<String> assetNames = new LinkedList<>();

		assetsJsonArray.forEach(asset -> {
			JsonObject asJsonObject = asset.getAsJsonObject();
			assetNames.add(asJsonObject.get("id").getAsString());
		});

		LocalDateTime startDateAtZeroAM = LocalDateTime.of(2021, 11, 30, 0, 0);
		LocalDateTime endDateAtOneAM = LocalDateTime.of(2021, 11, 30, 1, 0);

		ThreadPoolExecutor assetsPool = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(3));

		AtomicInteger counter = new AtomicInteger(0);

		ExecutorService es = Executors.newFixedThreadPool(3);
		List<Callable<Object>> todo = new ArrayList<Callable<Object>>();

		for (int i = 0; i < assetNames.size(); i++) {
			String asset = assetNames.get(i);
			todo.add(Executors.callable(new Runnable() {
				@Override
				public void run() {
					try {
						HttpResponse<String> responseAsset = Unirest
								.get("http://api.coincap.io/v2/assets/{asset}/history").header("accept", "*/*")
								.header("content-type", "application/json; charset=utf-8")
								.header("accept-encoding", "gzip, deflate")
								.header("Authorization", "Bearer eaa33432-5fff-4d6b-8e85-52e9897071e9")
								.routeParam("asset", asset)
								.queryString("interval", "d1")
								.socketTimeout(500)
								.queryString("start", startDateAtZeroAM.toInstant(ZoneOffset.UTC).toEpochMilli())
								.queryString("end", endDateAtOneAM.toInstant(ZoneOffset.UTC).toEpochMilli()).asString();
						Assert.assertNotNull(responseAsset);
						if (responseAsset.getStatus() == HttpStatus.REQUEST_TIMEOUT) {
							System.out.println("Passou do tempo " + asset);
						}
						Integer threadId = counter.incrementAndGet();

						System.out.println("ThreadID: " + threadId + " Body: " + responseAsset.getBody());
					} catch (UnirestException e) {
						if (e.getCause() instanceof SocketTimeoutException) {
							counter.incrementAndGet();
							log.info("Asset: "+ asset +"take more than 10 seconds");
						}
					}
				}

			}));
			if (todo.size() == 3 || (assetNames.size() == i)) {
				List<Future<Object>> answers;
				try {
					answers = es.invokeAll(todo, 30, TimeUnit.SECONDS);
					for (Future<Object> future : answers) {
						if (future.isDone()) {
							System.out.println("OK");
						}
					}
				} catch (InterruptedException e) {
					System.out.println("Thread Interrupted");
				}
				todo = new ArrayList<Callable<Object>>();
			}
		}

		assetNames.forEach(asset -> {
			assetsPool.execute(new Runnable() {
				@Override
				public void run() {
					counter.incrementAndGet();
					Integer threadId = counter.intValue();
					long startTime = System.currentTimeMillis();

					HttpResponse<String> responseAsset = Unirest.get("http://api.coincap.io/v2/assets/{asset}/history")
							.header("accept", "*/*").header("content-type", "application/json; charset=utf-8")
							.header("accept-encoding", "gzip, deflate")
							.header("Authorization", "Bearer eaa33432-5fff-4d6b-8e85-52e9897071e9")
							.routeParam("asset", asset).queryString("interval", "d1")
							.queryString("start", startDateAtZeroAM.toInstant(ZoneOffset.UTC).toEpochMilli())
							.queryString("end", endDateAtOneAM.toInstant(ZoneOffset.UTC).toEpochMilli()).asString();

					Assert.assertNotNull(responseAsset);

					JsonObject objBody = new Gson().fromJson(responseAsset.getBody(), JsonObject.class);

					Assert.assertTrue(objBody.isJsonObject());

					JsonArray dataJsonArray = objBody.get("data").getAsJsonArray();
					Assert.assertFalse(dataJsonArray.isEmpty());

					JsonObject firstElement = dataJsonArray.get(0).getAsJsonObject();
					Assert.assertNotNull(firstElement);
					Assert.assertNotNull(firstElement.get("priceUsd"));
					Assert.assertNotNull(firstElement.get("priceUsd").getAsBigDecimal());

					System.out.print("Asset: " + asset);
					System.out.println(" Price USD: " + firstElement.get("priceUsd").getAsBigDecimal());
					long endTime = System.currentTimeMillis();
					long totalTime = endTime - startTime;
					System.out.println("Thread " + threadId + " time execution: " + totalTime);
				}
			});
		});
	}

}
