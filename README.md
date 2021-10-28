# kafka
Exemple de Kafka: Produtores, Consumidores e streams

Exemplo seguido na forma Apache Kafka da Alura -> https://cursos.alura.com.br/formacao-kafka

###Configurações do Projeto:

<td>
  <li>Java 11</li>  
  <li>Maven</li>
</td>

###Dependências do Projeto (NA RAIZ TEMOS UM docker-compose):

<p><li>Zookeeper *latest</li>  
<p><li>Kafka *latest</li>  
<p><li>Kafkadrp (Ferramenta Visual para gerência do Kafka) *latest</li>  

###Iremos encontrar os seguintes serviços:

<li>common-database -> Possui o acesso ao Banco de Dados.</li>
<li>commons-kafka -> Possui o Produtor(KafkaDispatcher) e Consumidor(KafkaService) das mensagens.</li>
<li>service-email -> Responsável por 'Consumir' as mensagens de "Email", que são enviadas no tópico 'ECOMMERCE_SEND_EMAIL'.</li>
<li>service-email-new-order -> Responsável por 'Consumir' as mensagens de "Email para Nova Orders", que são enviadas no tópico 'ECOMMERCE_SEND_EMAIL'.</li>
<li>service-fraud-detector -> Responsável por "Produzir" as mensagens de 'Nova Venda' e 'Email'.</li>
<li>service-http-ecommerce -> Responsável gerar Novas Vendas.</li>
<li>service-log -> Responsável por 'Consumir' todas as mensagens, que são enviadas no tópico 'ECOMMERCE.*'.</li>
<li>service-new-order -> Responsável por 'Consumir' as mensagens de "Nova Venda", que são enviadas no tópico 'ECOMMERCE_NEW_ORDER'.</li>
<li>service-reading-report -> Responsável por 'Consumir' as mensagens de "Nova Venda", que são enviadas no tópico 'ECOMMERCE_NEW_ORDER'.</li>
<li>service-users -> Responsável por 'Consumir' as mensagens de "Nova Venda", que são enviadas no tópico 'ECOMMERCE_NEW_ORDER'.</li>

###Para testarmos o envio de 'Novas Orders' (service-http-ecommerce)
localhost:8080/new?email=seuEmail&amont=seuValor