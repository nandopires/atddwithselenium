# Atddwithselenium
Projeto criado para difundir o uso de ATDD com o intuito de agregar qualidade aos projetos web.
Com ele é possível criar um documento inteligível por qualquer pessoa, de qualquer área e validar uma página web, por exemplo a da classe de testes Bot - https://www.drogariaspacheco.com.br/

### 1 - O que o Bot faz?
1 - Carrega uma aba do navegador Chrome  
2 - Carrega a página da drogaria Pacheco com filtro de pesquisa o princípio Amoxicilina  
3 - Itera nas divs class descricao-prateleira para preencher uma collection de objetos da classe Produto  
4 - Carrega o texto do elemento class collection-link, representando o nome do medicamento, e atribui à propriedade itemName da classe Produto  
5 - Carrega o texto do elemento class valor-por, representando o valor do produto, e atribui à propriedade itemPrice da classe Produto  

## 2 - Driver do chrome
O driver do chome que está na pasta src/main/resources deve ser referenciado na classe de teste Bot, variável estática CHROME_DRIVER_PATH.
Exemplo: private static final String CHROME_DRIVER_PATH = "/home/user/workspace/atddwithselenium/src/main/resources/chromedriver";
O driver é para linux 64 bits e deve ter a instalação do Chrome Versão 98.0.4758.80 (Versão oficial) 64 bits.

## 3 - Wicket
O witcket é um framework leve e foi incluido no projeto para facilitar a demonstração da construção dos testes de aceitação com uma página criada no próprio projeto.

## 4 - Bot test
A classe Bot test valida o resultado de uma pesquisa do medicamento amoxicilina na página da Drogaria Pacheco - https://www.drogariaspacheco.com.br/ e carrega uma collection com os itens Produto, que possuem nome e preço.

## 5 - Pattern Page Object
A classe DrogariaPachecoPageObject representa a página de pesquisa da drogaria e segue o pattern PageObject recomendado pela documentação do Selenium.

## 6 - ATDD
Testes de aceitação escritos em markdown que instrumentam a API Concordion e fazem as validações definidas nos testes de aceitação, gerando como resultado arquivos HTML com o resultado dos testes.
