package experiments.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import experiments.model.Entry;

public class JsonUtils {

	public static List<Entry> load(String s) throws FileNotFoundException, IOException {
		Gson g = new Gson();
		Reader reader = new FileReader(s);
		Type type = new TypeToken<List<Entry>>() {
		}.getType();
		List<Entry> ll = g.fromJson(reader, type);
		reader.close();
		return ll;
	}

}
