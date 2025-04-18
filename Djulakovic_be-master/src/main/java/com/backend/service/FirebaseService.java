package com.backend.service;

import com.google.auth.oauth2.*;
import com.google.firebase.*;
import com.google.firebase.messaging.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

import static org.apache.commons.lang3.StringUtils.*;

@Service
public class FirebaseService {

	private static final Log LOG = LogFactory.getLog(FirebaseService.class);

	@Value("${app.app-name}")
	private String appName;

	@Autowired
	public FirebaseService() {
		try {
//			InputStream refreshToken = ResourceUtils.getURL(  )
//					.openStream();
			InputStream refreshToken = getClass().getResourceAsStream("/firebase.json");
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(refreshToken))
					.build();
			initializeFirebase(options);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	public void sendDriverMessage(String firebaseToken, FirebaseAction action) {
		sendMobileMessage(firebaseToken, action.toString(), action.getMessage(), false);
	}

	public void sendFmMessage(String firebaseToken, FirebaseAction action, String title, String message) {
		sendWebMessage(firebaseToken, action.toString(), title, message);
	}

	public void sendNotification(String driverFirebaseToken, FirebaseAction action) {
		sendMobileMessage(driverFirebaseToken, action.toString(), action.getMessage(), true);
	}

	public void sendNotificationWithExtendedMessage(String driverFirebaseToken, FirebaseAction action,
	                                                String additionalMessage) {
		sendMobileMessage(driverFirebaseToken, action.toString(), action.getMessage() + additionalMessage, true);
	}

	private void initializeFirebase(FirebaseOptions options) {
		if (FirebaseApp.getApps()
				.isEmpty()) {
			FirebaseApp.initializeApp(options);
			LOG.info("Firebase application has been initialized");
		}
	}

	private void sendMobileMessage(String firebaseToken, String key, String body, boolean sendNotification) {
		if (isEmpty(firebaseToken)) {
			return;
		}

		Message message;

		if(sendNotification) {
			message = Message.builder()
					.setNotification(Notification.builder()
							.setTitle(appName)
							.setBody(body)
							.build())
					.setAndroidConfig(AndroidConfig.builder()
							.setPriority(AndroidConfig.Priority.HIGH)
							.setNotification(AndroidNotification.builder()
									.setPriority(AndroidNotification.Priority.HIGH)
									.setChannelId("ch_data")
									.setDefaultSound(true)
									.setDefaultVibrateTimings(true)
									.setDefaultLightSettings(true)
									.build())
							.build())
					.setToken(firebaseToken)
					.putData("key", key)
					.putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
					.build();
		} else {
			message = Message.builder()
					.setToken(firebaseToken)
					.putData("key", key)
					.build();
		}

		FirebaseMessaging.getInstance()
				.sendAsync(message);

		LOG.info("sendMobileMessage: fbToken: " + firebaseToken + ",  key: " + key + ",  body: " + body);
	}

	private void sendWebMessage(String firebaseToken, String key, String title, String body) {
		if (isEmpty(firebaseToken)) {
			return;
		}

		if (isEmpty(title)) {
			title = appName;
		}

		Message message = Message.builder()
				.setToken(firebaseToken)
				.putData("key", key)
				.putData("title", title)
				.putData("body", body)
//				.putData("icon", featureSwitchProvider.getUrl() + "/notification-logo.png")
//				.putData("data", "{\"url\": \"" + featureSwitchProvider.getUrl() + "\"}")
				.build();

		FirebaseMessaging.getInstance()
				.sendAsync(message);

	}
}
