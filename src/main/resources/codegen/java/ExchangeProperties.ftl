
exchange.names=${inlineExchangeNames}

<#list exchangeNames as exchangeName>
exchange.${exchangeName}.hostname=localhost
exchange.${exchangeName}.username=guest
exchange.${exchangeName}.password=guest
exchange.${exchangeName}.port=5762
exchange.${exchangeName}.virtual.host=/

</#list>