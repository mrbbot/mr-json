# MrJSON

There are some many formats for data communication and storage these days, but JSON (or at least JSON like) seems one of the most (if not the most) popular. There are also many different libraries for parsing and writing JSON. So why write a new one?

**Simplicity.**

MrJSON doesn't require builder objects or factories and the like, instead parsing JSON can be done from one single line:

```java
JSONObject obj = JSON.parseObject("{ \"messages\": [ { \"content\": \"Hello!\", \"level\": 4 } ] }");

obj.getArray("messages").get(0).getString("content"); // => "Hello!"
```

Or straight from a file:

```java
JSONObject obj = JSON.parseObject(new File("hello.json"));
```

Or even a URL:

```java
JSONObject obj = JSON.parseObject(new URL("https://jsonplaceholder.typicode.com/posts"));
```

Or a map or list:

```java
HashMap<String, Object> map = new HashMap<>();

ArrayList<HashMap<String, Object>> messages = new ArrayList<>();

HashMap<String, Object> message = new HashMap<>();
message.put("content", "Hello");
message.put("level", 4);
messages.add(message);

map.put("messages", messages);

JSONObject obj = JSON.fromMap(map);
```

Writing JSON is just as easy:

```java
JSONObject root = new JSONObject();
        
JSONArray messages = new JSONArray();

JSONObject message = new JSONObject();
message.put("content", "Hello!");
message.put("level", 4);
messages.add(message);

root.put("messages", messages);

root.toString(); // => "{ "messages": [ { "content": "Hello!", "level": 4 } ] }"
```

You can even save the result to a file directly:

```java
root.toFile("hello.json");
```