package com.minecraftmarket.minecraftmarket.common.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minecraftmarket.minecraftmarket.common.api.models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MCMarketApi {
    private final String BASE_URL = "https://www.minecraftmarket.com/api/v1/plugin/";
    private final ObjectMapper MAPPER = new ObjectMapper();
    private final String API_KEY;
    private final String USER_AGENT;
    private final boolean DEBUG;

    public MCMarketApi(String apiKey, String userAgent, boolean debug) {
        API_KEY = apiKey;
        USER_AGENT = userAgent;
        DEBUG = debug;
    }

    public String getBaseUrl() {
        return BASE_URL;
    }

    public String getApiKey() {
        return API_KEY;
    }

    public String getUserAgent() {
        return USER_AGENT;
    }

    public boolean authAPI() {
        try {
            makeRequest("/market", "GET", "");
            return true;
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public Market getMarket() {
        try {
            BufferedReader reader = makeRequest("/market", "GET", "");
            return MAPPER.readValue(reader, Market.class);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public long getCategoriesCount() {
        try {
            BufferedReader reader = makeRequest("/categories", "GET", "");
            JsonNode response = MAPPER.readTree(reader);
            return response.get("count").asLong();
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public List<Category> getCategories() {
        return getCategories(1, 0);
    }

    public List<Category> getCategories(int startPage, int maxPages) {
        List<Category> categories = new ArrayList<>();
        try {
            BufferedReader reader = makeRequest("/categories", "GET", "&limit=25");
            JsonNode response = MAPPER.readTree(reader);
            long count = response.get("count").asLong();
            long pages = (count / 25) + 1;

            if (startPage <= pages) {
                pages = pages - (startPage - 1);
            } else throw new IndexOutOfBoundsException("startPage exceeds the total amount of pages.");

            if (maxPages > 0 && pages > maxPages) {
                pages = maxPages;
            }

            for (int i = startPage; i <= pages; i++) {
                if (i > 1) {
                    reader = makeRequest("/categories", "GET", "&limit=25&offset=" + (25 * (i - 1)));
                    response = MAPPER.readTree(reader);
                }

                Iterator<JsonNode> results = response.get("results").elements();
                while (results.hasNext()) {
                    JsonNode result = results.next();
                    Category category = getCategory(result.get("id").asLong());
                    if (category != null) {
                        categories.add(category);
                    }
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return categories;
    }

    public Category getCategory(long categoryID) {
        try {
            BufferedReader reader = makeRequest(String.format("/categories/%s", categoryID), "GET", "");
            JsonNode response = MAPPER.readTree(reader);
            long id = response.get("id").asLong();
            String name = response.get("name").asText();
            String description = response.get("gui_description").asText();
            String icon = response.get("gui_icon").asText();
            long order = response.get("order").asLong();

            List<Category> subCategories = new ArrayList<>();
            Iterator<JsonNode> subCategoriesResult = response.get("subcategories").elements();
            while (subCategoriesResult.hasNext()) {
                JsonNode subCategory = subCategoriesResult.next();
                Category category = getCategory(subCategory.get("id").asLong());
                if (category != null) {
                    subCategories.add(category);
                }
            }

            return new Category(id, name, description, icon, subCategories, getItems(id), order);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public long getItemsCount() {
        return getItemsCount(0);
    }

    public long getItemsCount(long categoryID) {
        try {
            BufferedReader reader = makeRequest("/items", "GET", categoryID > 0 ? "&category=" + categoryID : "");
            JsonNode response = MAPPER.readTree(reader);
            return response.get("count").asLong();
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public List<Item> getItems() {
        return getItems(0);
    }

    public List<Item> getItems(long categoryID) {
        return getItems(categoryID, 1, 0);
    }

    public List<Item> getItems(long categoryID, int startPage, int maxPages) {
        List<Item> items = new ArrayList<>();
        try {
            BufferedReader reader = makeRequest("/items", "GET", "&limit=25" + (categoryID > 0 ? "&category=" + categoryID : ""));
            JsonNode response = MAPPER.readTree(reader);
            long count = response.get("count").asLong();
            long pages = (count / 25) + 1;

            if (startPage <= pages) {
                pages = pages - (startPage - 1);
            } else throw new IndexOutOfBoundsException("startPage exceeds the total amount of pages.");

            if (maxPages > 0 && pages > maxPages) {
                pages = maxPages;
            }

            for (int i = startPage; i <= pages; i++) {
                if (i > 1) {
                    reader = makeRequest("/items", "GET", "&limit=25&offset=" + (25 * (i - 1)) + (categoryID > 0 ? "&category=" + categoryID : ""));
                    response = MAPPER.readTree(reader);
                }

                Iterator<JsonNode> results = response.get("results").elements();
                while (results.hasNext()) {
                    JsonNode result = results.next();
                    items.add(MAPPER.readValue(result.toString(), Item.class));
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return items;
    }

    public Item getItem(long itemID) {
        try {
            BufferedReader reader = makeRequest(String.format("/items/%s", itemID), "GET", "");
            return MAPPER.readValue(reader, Item.class);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public long getTransactionsCount() {
        return getTransactionsCount(null);
    }

    public long getTransactionsCount(TransactionStatus transactionStatus) {
        try {
            String query = buildQueryFromFilter(transactionStatus);
            BufferedReader reader = makeRequest("/transactions", "GET", query);
            JsonNode response = MAPPER.readTree(reader);
            return response.get("count").asLong();
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public List<Transaction> getTransactions() {
        return getTransactions(1,0);
    }

    public List<Transaction> getTransactions(int startPage, int maxPages) {
        return getTransactions(null, startPage, maxPages);
    }

    public List<Transaction> getTransactions(TransactionStatus transactionStatus) {
        return getTransactions(transactionStatus, 1, 0);
    }

    public List<Transaction> getTransactions(TransactionStatus transactionStatus, int startPage, int maxPages) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            String query = buildQueryFromFilter(transactionStatus);
            BufferedReader reader = makeRequest("/transactions", "GET", "&limit=25" + query);
            JsonNode response = MAPPER.readTree(reader);
            long count = response.get("count").asLong();
            long pages = (count / 25) + 1;

            if (startPage <= pages) {
                pages = pages - (startPage - 1);
            } else throw new IndexOutOfBoundsException("startPage exceeds the total amount of pages.");

            if (maxPages > 0 && pages > maxPages) {
                pages = maxPages;
            }

            for (int i = startPage; i <= pages; i++) {
                if (i > 1) {
                    reader = makeRequest("/transactions", "GET", "&limit=25&offset=" + (25 * (i - 1)) + query);
                    response = MAPPER.readTree(reader);
                }

                Iterator<JsonNode> results = response.get("results").elements();
                while (results.hasNext()) {
                    JsonNode result = results.next();
                    Transaction transaction = getTransaction(result.get("id").asLong());
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return transactions;
    }

    public Transaction getTransaction(long transactionID) {
        try {
            BufferedReader reader = makeRequest(String.format("/transactions/%s", transactionID), "GET", "");
            JsonNode response = MAPPER.readTree(reader);
            long id = response.get("id").asLong();
            String status = response.get("status").asText();
            String gateway = response.get("gateway").asText();
            String transaction_id = response.get("transaction_id").asText();
            String price = response.get("price").asText();
            String date = response.get("date").asText();
            
            Currency currency = MAPPER.readValue(response.get("currency").toString(), Currency.class);
            MMPlayer player = MAPPER.readValue(response.get("player").toString(), MMPlayer.class);

            List<Purchase> purchases = new ArrayList<>();
            Iterator<JsonNode> purchasesResult = response.get("purchases").elements();
            while (purchasesResult.hasNext()) {
                Purchase purchase = getPurchase(purchasesResult.next().get("id").asLong());
                if (purchase != null) {
                    purchases.add(purchase);
                }
            }

            return new Transaction(id, status, gateway, transaction_id, price, currency, date, player, purchases);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public long getPurchasesCount() {
        try {
            BufferedReader reader = makeRequest("/purchases", "GET", "");
            JsonNode response = MAPPER.readTree(reader);
            return response.get("count").asLong();
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public List<Purchase> getPurchases() {
        return getPurchases(1, 0);
    }

    public List<Purchase> getPurchases(int startPage, int maxPages) {
        List<Purchase> purchases = new ArrayList<>();
        try {
            BufferedReader reader = makeRequest("/purchases", "GET", "&limit=25");
            JsonNode response = MAPPER.readTree(reader);
            long count = response.get("count").asLong();
            long pages = (count / 25) + 1;

            if (startPage <= pages) {
                pages = pages - (startPage - 1);
            } else throw new IndexOutOfBoundsException("startPage exceeds the total amount of pages.");

            if (maxPages > 0 && pages > maxPages) {
                pages = maxPages;
            }

            for (int i = startPage; i <= pages; i++) {
                if (i > 1) {
                    reader = makeRequest("/purchases", "GET", "&limit=25&offset=" + (25 * (i - 1)));
                    response = MAPPER.readTree(reader);
                }

                Iterator<JsonNode> results = response.get("results").elements();
                while (results.hasNext()) {
                    JsonNode result = results.next();
                    purchases.add(MAPPER.readValue(result.toString(), Purchase.class));
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return purchases;
    }

    public Purchase getPurchase(long purchaseID) {
        try {
            BufferedReader reader = makeRequest(String.format("/purchases/%s", purchaseID), "GET", "");
            return MAPPER.readValue(reader, Purchase.class);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public long getCommandsCount() {
        return getCommandsCount(null);
    }

    public long getCommandsCount(CommandType commandType) {
        try {
            String query = buildQueryFromFilter(commandType);
            BufferedReader reader = makeRequest("/commands", "GET", query);
            JsonNode response = MAPPER.readTree(reader);
            return response.get("count").asLong();
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public List<Command> getCommands() {
        return getCommands(1, 0);
    }

    public List<Command> getCommands(int startPage, int maxPages) {
        return getCommands(null, startPage, maxPages);
    }

    public List<Command> getCommands(CommandType commandType) {
        return getCommands(commandType, 1, 0);
    }

    public List<Command> getCommands(CommandType commandType, int startPage, int maxPages) {
        List<Command> commands = new ArrayList<>();
        try {
            String query = buildQueryFromFilter(commandType);
            BufferedReader reader = makeRequest("/commands", "GET", "&limit=25" + query);
            JsonNode response = MAPPER.readTree(reader);
            long count = response.get("count").asLong();
            long pages = (count / 25) + 1;

            if (startPage <= pages) {
                pages = pages - (startPage - 1);
            } else throw new IndexOutOfBoundsException("startPage exceeds the total amount of pages.");

            if (maxPages > 0 && pages > maxPages) {
                pages = maxPages;
            }

            for (int i = startPage; i <= pages; i++) {
                if (i > 1) {
                    reader = makeRequest("/commands", "GET", "&limit=25&offset=" + (25 * (i - 1)) + query);
                    response = MAPPER.readTree(reader);
                }

                Iterator<JsonNode> results = response.get("results").elements();
                while (results.hasNext()) {
                    JsonNode result = results.next();
                    commands.add(MAPPER.readValue(result.toString(), Command.class));
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return commands;
    }

    public Command getCommand(long commandID) {
        try {
            BufferedReader reader = makeRequest(String.format("/commands/%s", commandID), "GET", "");
            return MAPPER.readValue(reader, Command.class);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean setExecuted(long commandID) {
        try {
            makeRequest(String.format("/commands/%s", commandID), "PUT", "{\"executed\":1}");
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public long getEventsCount() {
        try {
            BufferedReader reader = makeRequest("/events", "GET", "");
            JsonNode response = MAPPER.readTree(reader);
            return response.get("count").asLong();
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public List<Event> getEvents() {
        return getEvents(1, 0);
    }

    public List<Event> getEvents(int startPage, int maxPages) {
        List<Event> events = new ArrayList<>();
        try {
            BufferedReader reader = makeRequest("/events", "GET", "&limit=25");
            JsonNode response = MAPPER.readTree(reader);
            long count = response.get("count").asLong();
            long pages = (count / 25) + 1;

            if (startPage <= pages) {
                pages = pages - (startPage - 1);
            } else throw new IndexOutOfBoundsException("startPage exceeds the total amount of pages.");

            if (maxPages > 0 && pages > maxPages) {
                pages = maxPages;
            }

            for (int i = startPage; i <= pages; i++) {
                if (i > 1) {
                    reader = makeRequest("/events", "GET", "&limit=25&offset=" + (25 * (i - 1)));
                    response = MAPPER.readTree(reader);
                }

                Iterator<JsonNode> results = response.get("results").elements();
                while (results.hasNext()) {
                    JsonNode result = results.next();
                    events.add(MAPPER.readValue(result.toString(), Event.class));
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return events;
    }

    public Event getEvent(long eventID) {
        try {
            BufferedReader reader = makeRequest(String.format("/events/%s", eventID), "GET", "");
            return MAPPER.readValue(reader, Event.class);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean sendEvents(List<Event> events) {
        try {
            makeRequest("/events", "POST", MAPPER.writeValueAsString(events));
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    private BufferedReader makeRequest(String url, String method, String query) throws IOException {
        HttpURLConnection conn;
        if (method.equals("POST") || method.equals("PUT")) {
            conn = (HttpURLConnection) new URL(BASE_URL + API_KEY + url + "?format=json").openConnection();
            conn.setRequestMethod(method);
        } else {
            conn = (HttpURLConnection) new URL(BASE_URL + API_KEY + url + "?format=json" + query).openConnection();
            conn.setRequestMethod("GET");
        }

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setUseCaches(false);
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(10000);
        conn.setDoInput(true);

        if (method.equals("POST") || method.equals("PUT")) {
            conn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(query);
            out.close();
            conn.getInputStream().close();
            return null;
        }

        return new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    private String buildQueryFromFilter(Filter filter) {
        if (filter != null) {
            return "&" + filter.getName() + "=" + filter.getValue();
        }
        return "";
    }

    public enum TransactionStatus implements Filter {
        COMPLETED("1"),
        PENDING("2"),
        CHARGEBACK("3"),
        REFUNDED("4"),
        ERROR("5");

        private final String value;

        TransactionStatus(String value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return "status";
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public enum CommandType implements Filter {
        INITIAL("initial"),
        EXPIRY("expiry"),
        CHARGEBACK("chargeback"),
        REFUND("refund"),
        RENEWAL("renewal");

        private final String value;

        CommandType(String value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return "type";
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public enum CommandStatus implements Filter {
        EXECUTED("true"),
        NOT_EXECUTED("false");

        private final String value;

        CommandStatus(String value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return "executed";
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public interface Filter {
        String getName();
        String getValue();
    }
}