package com.example.henriwilander.viikko9;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ListView listView;
    ListView listView2;
    ArrayList<String> listItems;
    ArrayList<Cinema> cinema_array;
    ArrayList<String> arraySpinner;
    ArrayList<String> listItems2;
    EditText movieName;
    EditText dateTime;
    EditText time1;
    EditText time2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        listView = findViewById(R.id.listview);
        listView2 = findViewById(R.id.listview2);
        listItems = new ArrayList();
        listItems2 = new ArrayList();
        this.arraySpinner = new ArrayList<String>();
        arraySpinner.add("Valitse teatteri:");
        this.cinema_array = new ArrayList();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String urlString = "https://www.finnkino.fi/xml/TheatreAreas/";
            Document doc = builder.parse(urlString);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getDocumentElement().getElementsByTagName("TheatreArea");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    int id = Integer.parseInt(element.getElementsByTagName("ID").item(0).getTextContent());
                    String name = element.getElementsByTagName("Name").item(0).getTextContent();
                    if (!name.equals("Pääkaupunkiseutu") && !name.equals("Espoo") && !name.equals("Helsinki") && !name.equals("Tampere") && !name.equals("Valitse alue/teatteri")) {
                        cinema_array.add(new Cinema(id, name));
                        arraySpinner.add(name);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    @Override

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        listItems.clear();
        listItems2.clear();
        String text = parent.getItemAtPosition(position).toString();
        String headerText = "Valitse teatteri:";
        if (!text.equals(headerText)) {
            searchId(text);
        } else {
            printTextToList2(0, text);
            listItems.add("");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
            listView.setAdapter(arrayAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void printTextToList(int id) {
        dateTime = findViewById(R.id.editText);
        time1 = findViewById(R.id.editText2);
        time2 = findViewById(R.id.editText4);
        try {
            String print_to_list;
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String date = dateTime.getText().toString();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date firstChoice = parser.parse(time1.getText().toString());
            Date secondChoice = parser.parse(time2.getText().toString());
            String urlString = "http://www.finnkino.fi/xml/Schedule/?area=" + id + "&dt=" + date;
            Document doc = builder.parse(urlString);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getDocumentElement().getElementsByTagName("Show");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String title = element.getElementsByTagName("Title").item(0).getTextContent();
                    String pvm = element.getElementsByTagName("dttmShowStart").item(0).getTextContent();
                    String auditorium = element.getElementsByTagName("TheatreAuditorium").item(0).getTextContent();
                    String[] result = pvm.split("T");
                    String time = result[1].substring(0, 5);
                    String[] newDate = result[0].split("-");
                    String newDate1 = newDate[2] + "." + newDate[1] + "." + newDate[0];
                    try {
                        Date userDate = parser.parse(time);
                        if (userDate.after(firstChoice) && userDate.before(secondChoice)) {
                            print_to_list = (auditorium + ": " + "Elokuva: " + title + " Päivämäärä: " + newDate1 + " klo: " + time);
                            listItems.add(print_to_list);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
            listView.setAdapter(arrayAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void searchId(String name) {
        for (Cinema a : cinema_array) {
            if (a.getName().equals(name)) {
                printTextToList(a.getId());
                printTextToList2(a.getId(), a.getName());
            }
        }
    }

    public void printTextToList2(int cinemaId, String name) {
        dateTime = findViewById(R.id.editText);
        time1 = findViewById(R.id.editText2);
        time2 = findViewById(R.id.editText4);
        movieName = findViewById(R.id.editText5);
        int id;
        try {
            String print_to_list;
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String date = dateTime.getText().toString();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date firstChoice = parser.parse(time1.getText().toString());
            Date secondChoice = parser.parse(time2.getText().toString());
            listItems2.add(movieName.getText().toString());
            if (cinemaId != 0) {
                id = cinemaId;
                String urlString = "http://www.finnkino.fi/xml/Schedule/?area=" + id + "&dt=" + date;
                Document doc = builder.parse(urlString);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getDocumentElement().getElementsByTagName("Show");
                for (int i = 0; i < nList.getLength(); i++) {
                    Node node = nList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String title = element.getElementsByTagName("Title").item(0).getTextContent();
                        String pvm = element.getElementsByTagName("dttmShowStart").item(0).getTextContent();
                        String[] result = pvm.split("T");
                        String time = result[1].substring(0, 5);
                        String[] newDate = result[0].split("-");
                        String newDate1 = newDate[2] + "." + newDate[1] + "." + newDate[0];
                        try {
                            Date userDate = parser.parse(time);
                            if (userDate.after(firstChoice) && userDate.before(secondChoice) && movieName.getText().toString().equals(title)) {
                                print_to_list = (name + ": " + " Päivämäärä: " + newDate1 + " klo: " + time);
                                listItems2.add(print_to_list);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                for (Cinema a : cinema_array) {
                    id = a.getId();
                    String urlString = "http://www.finnkino.fi/xml/Schedule/?area=" + id + "&dt=" + date;
                    Document doc = builder.parse(urlString);
                    doc.getDocumentElement().normalize();
                    NodeList nList = doc.getDocumentElement().getElementsByTagName("Show");
                    for (int i = 0; i < nList.getLength(); i++) {
                        Node node = nList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            String title = element.getElementsByTagName("Title").item(0).getTextContent();
                            String pvm = element.getElementsByTagName("dttmShowStart").item(0).getTextContent();
                            String[] result = pvm.split("T");
                            String time = result[1].substring(0, 5);
                            String[] newDate = result[0].split("-");
                            String newDate1 = newDate[2] + "." + newDate[1] + "." + newDate[0];
                            try {
                                Date userDate = parser.parse(time);
                                if (userDate.after(firstChoice) && userDate.before(secondChoice) && movieName.getText().toString().equals(title)) {
                                    print_to_list = (a.getName() + ": " + " Päivämäärä: " + newDate1 + " klo: " + time);
                                    listItems2.add(print_to_list);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems2);
            listView2.setAdapter(arrayAdapter);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}