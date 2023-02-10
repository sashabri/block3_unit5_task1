import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static List<Employee> parseCSV(String[] arr, String name) {
        List<Employee> result = null;

        try (CSVReader reader = new CSVReader(new FileReader(name))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(arr);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            result = csv.parse();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String listToJson(List<Employee> list) {
        String result;

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        Type listType = new TypeToken<List<Employee>>() {
        }.getType();

        result = gson.toJson(list, listType);

        return result;
    }

    private static void writeString(String json) {
        try (FileWriter writer = new FileWriter("data2.json")) {

            writer.write(json);
            writer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String fileName) {
        List<Employee> result = new ArrayList<Employee>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));


            NodeList nodeList = document.getElementsByTagName("employee");
            for (int i = 0; i < nodeList.getLength(); i++) {
                result.add(getEmployee(nodeList.item(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static Employee getEmployee(Node node) {
        Employee employee = new Employee();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            employee.setId(Long.parseLong(getTagValue("id", element)));
            employee.setFirstName(getTagValue("firstName", element));
            employee.setLastName(getTagValue("lastName", element));
            employee.setCountry(getTagValue("country", element));
            employee.setAge(Integer.parseInt(getTagValue("age", element)));
        }
        return employee;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    private static String readString(String fileLocation) {
        String result = "";


        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileLocation))) {
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                result = result + str;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static List<Employee> jsonToList(String json) {

        List<Employee> employeeList = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(json);

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            for (int i = 0; i < array.size(); i++) {
                if (array.get(i) != null) {
                    Employee employee = gson.fromJson(array.get(i).toString(), Employee.class);
                    employeeList.add(employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json);

        String nameXml = "data.xml";
        List<Employee> list2 = parseXML(nameXml);
        String xml = listToJson(list2);
        writeString(xml);

        String json2 = readString("data.json");
        List<Employee> employee = jsonToList(json2);

        for (Employee employees : employee) {
            System.out.println(employee);
        }

    }
}
