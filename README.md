# kafka
Exemple de Kafka: Produtores, Consumidores e streams

Exemplo seguido na forma Apache Kafka da Alura -> https://cursos.alura.com.br/formacao-kafka

Configurações do Projeto:

<t>
  <li>Java 11</li>  
  <li>Maven</li>  

Dependências do Projeto (NA RAIZ TEMOS UM docker-compose):

<p>
  <li>Zookeeper *latest</li>  
  <li>Kafka *latest</li>  
  <li>Kafkadrp (Ferramenta Visual para gerência do Kafka) *latest</li>  
</p>

Iremos encontrar os seguintes serviços:

<li>commons-kafka -> Possui o Produtor(KafkaDispatcher) e Consumidor(KafkaService) das mensagens.</li>
<li>service-fraud-detector -> Responsábel por "Produzir" as mensagens de 'Nova Venda' e 'Email'.</li>
<li>service-email -> Responsável por 'Consumir' as mensagens de "Email", que são enviadas no tópico 'ECOMMERCE_SEND_EMAIL'.</li>
<li>service-new-order -> Responsável por 'Consumir' as mensagens de "Nova Venda", que são enviadas no tópico 'ECOMMERCE_NEW_ORDER'.</li>
<li>service-log -> Responsável por 'Consumir' todas as mensagens, que são enviadas no tópico 'ECOMMERCE.*'.</li>
