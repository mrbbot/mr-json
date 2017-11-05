package com.mrbbot.mrjson;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

class JSONTest {
    @Test
    void testBigFile() throws MalformedURLException {
        JSONObject mcVersions = JSON.parseObject(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json"));

        mcVersions.toFile("big.json");
    }

    @Test
    void testRead() {
        JSONObject object = JSON.parseObject(new File("simple.json"));

        JSONObject obj = JSON.parseObject("{ \"messages\": [ { \"content\": \"Hello!\", \"level\": 4 } ] }");
        System.out.println(obj.getArray("messages").get(0).getString("content"));

        assert object.getArray("friends").get(0).getString("test2").equals("yay!");
    }

    @Test
    void testWrite() {
        JSONObject root = new JSONObject();

        /*JSONArray messages = new JSONArray();

        JSONObject message = new JSONObject();

        message.put("content", "Hello!");
        message.put("level", 4);

        messages.add(message);

        root.put("messages", messages);

        System.out.println(root.toString());
        root.toFile("hello.json");*/

        root.put("favourite-colour", "blue");
        root.put("age", 15);

        JSONArray likes = new JSONArray();
        likes.add("computers");
        likes.add("programming");
        root.put("likes", likes);

        root.toFile("hello.json");

        assert root.toString().equals("{\n" +
                "    \"age\": 15,\n" +
                "    \"favourite-colour\": \"blue\",\n" +
                "    \"likes\": [\n" +
                "        \"computers\",\n" +
                "        \"programming\" \n" +
                "    ] \n" +
                "}");
    }

    @Test
    void testMap() {
        HashMap<String, Object> map = new HashMap<>();

        ArrayList<HashMap<String, Object>> messages = new ArrayList<>();

        HashMap<String, Object> message = new HashMap<>();
        message.put("content", "Hello");
        message.put("level", 4);
        messages.add(message);

        map.put("messages", messages);

        System.out.println(JSON.fromMap(map).toString());
    }

}
