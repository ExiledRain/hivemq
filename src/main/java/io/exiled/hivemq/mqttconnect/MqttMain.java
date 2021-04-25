package io.exiled.hivemq.mqttconnect;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

public class MqttMain {

    public static void main(String[] args) throws Exception {
        //Connect to broker in cloud
        final String host = "faf7fad050db472b86be2da74ed9776c.s1.eu.hivemq.cloud";
        final String username = "username";
        final String password = "password";
        final int port = 8883;

        //create Mqtt client
        final Mqtt5BlockingClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(port)
                .sslWithDefaultConfig()
                .buildBlocking();

        //connect to HiveMq cloud with TLS and username/pw
        Mqtt5ConnAck send = client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .send();

        System.out.println("Connected successfully");

        //subscribe to the topic "my/test/topic"
        client.subscribeWith()
                .topicFilter("my/test/topic")
                .send();

        //set a callback that is called when a message is received (using the async API style)
        client.toAsync().publishes(ALL, publish -> {
            System.out.println("Received message : " + publish.getTopic() + " -> " + UTF_8.decode(publish.getPayload().get()));

            //disconnect the client after a message was received
            client.disconnect();
        });

        //publish a message to the topic "my/test/topic"
        client.publishWith()
                .topic("my/test/topic")
                .payload(UTF_8.encode("Hello"))
                .send();
    }
}
