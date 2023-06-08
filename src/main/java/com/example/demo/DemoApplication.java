package com.example.demo;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.OAuth2ClientCredentialsGrantCredentialsProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class DemoApplication {
	@Bean
	public void connect() {
		CredentialsProvider credentialsProvider =
				new OAuth2ClientCredentialsGrantCredentialsProvider.OAuth2ClientCredentialsGrantCredentialsProviderBuilder()
						.tokenEndpointUri("http://localhost:8080/oauth/token/")
						.clientId("webappclient").clientSecret("webappclientsecret")
						.grantType("password")
						.parameter("username", "appuser")
						.parameter("password", "appusersecret")
						.build();


		ConnectionFactory factory = new ConnectionFactory();

		factory.setPassword(credentialsProvider.getPassword());

		try {
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

//			 Publish the message to the exchange
			channel.basicPublish("test", "test", null, "test".getBytes("UTF-8"));

			System.out.println("Message sent successfully to exchange test!");


			channel.basicPublish("test2", "test2", null, "test2".getBytes("UTF-8"));

			System.out.println("Message sent successfully to exchange test2!");

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
					String message = null;
					try {
						message = new String(body, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
					System.out.println("Received message from queue test: " + message);
				}
			};

			Consumer consumer2 = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
					String message = null;
					try {
						message = new String(body, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
					System.out.println("Received message from queue test2: " + message);
				}
			};

			try {
				// Start consuming messages from the queue
				channel.basicConsume("test", true, consumer);
			} catch (Exception e) {
				System.out.println("Can not read from test!");
			}

			try {
				// Start consuming messages from the queue
				channel.basicConsume("test2", true, consumer);
			} catch (Exception e) {
				System.out.println("Can not read from test2!");
			}

			System.out.println("Waiting for messages...");
			// Keep the program running to continue receiving messages
			Thread.sleep(Long.MAX_VALUE);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("Error occured");
		}
	}


		public static void main(String[] args) throws IOException, TimeoutException {
		SpringApplication.run(DemoApplication.class, args);





	}

}
