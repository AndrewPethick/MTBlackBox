package application;

import java.util.HashMap;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Icons {
	private final static HashMap<String, Image> cached = new HashMap<String, Image>();
	public final static ImageView createIcon(String iconName, int size) {
		if (cached.containsKey(iconName)) {
			ImageView view = new ImageView(cached.get(iconName));
			view.setFitWidth(size);
			view.setPreserveRatio(true);
			view.setSmooth(true);
			view.setCache(true);
			return view;
		} else {
			String url = Icons.class.getResource(iconName).toExternalForm();
			Image i = new Image(url);
			ImageView view = new ImageView(i);
			view.setFitWidth(size);
			view.setPreserveRatio(true);
			view.setSmooth(true);
			view.setCache(true);
			cached.put(iconName, i);
			return view;
		}

	}
}
