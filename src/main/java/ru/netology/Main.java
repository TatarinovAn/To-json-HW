package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws Exception {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCVS = parseCSV(columnMapping, fileName);
        String json = listToJson(listCVS);
        writeString(json, "data.json");


        List<Employee> listXML = parseXML("data.xml");
        writeString(listToJson(listXML), "data2.json");

    }

    public static List<Employee> parseCSV(String[] mapping, String fileName) throws Exception {
        List<Employee> staff;
        CSVReader csvReader = new CSVReader(new FileReader(fileName));
        var strategy = new ColumnPositionMappingStrategy<Employee>();
        strategy.setType(Employee.class);
        strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");

        CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                .withMappingStrategy(strategy)
                .build();

        staff = csv.parse();
        return staff;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException,
            IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(new File(fileName));
        Element element = doc.getDocumentElement();

        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NodeList nodeListChild = nodeList.item(i).getChildNodes();
                String[] arrayV = new String[nodeListChild.getLength()];

                long id = Long.parseLong(nodeListChild.item(1).getTextContent());
                String firstName = nodeListChild.item(3).getTextContent();
                String lastName = nodeListChild.item(5).getTextContent();
                String country = nodeListChild.item(7).getTextContent();
                int age = Integer.parseInt(nodeListChild.item(9).getTextContent());
                list.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return list;
    }
}
