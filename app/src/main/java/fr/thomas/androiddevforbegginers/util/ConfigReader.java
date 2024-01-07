package fr.thomas.androiddevforbegginers.util;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fr.thomas.androiddevforbegginers.R;

public class ConfigReader {

    private Properties properties;

    public ConfigReader(AppCompatActivity context) {
        Properties properties = new Properties();

        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.config);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }
}
