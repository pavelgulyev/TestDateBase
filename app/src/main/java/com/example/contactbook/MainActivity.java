package com.example.contactbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Person> persons;
    ListView contacts;
    PersonAdapter personsAdapter;
    EditText search;
    DatabaseAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, EditPerson.class);
        contacts = findViewById(R.id.list);
        search = findViewById(R.id.editTextSearch);
        adapter = new DatabaseAdapter(this);
        AdapterView.OnItemClickListener itemListener = (parent, v, position, id) -> {
            Person selectedPerson = (Person) parent.getItemAtPosition(position);
            intent.putExtra("id",selectedPerson.getId());
            startActivity(intent);
        };
        contacts.setOnItemClickListener(itemListener);
        // установка слушателя изменения текста
        persons = new ArrayList<>();
        personsAdapter = new PersonAdapter(this, R.layout.list_item, persons);
        contacts.setAdapter(personsAdapter);
        search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.open();
                persons.clear();
                if(s == null || s.length() == 0) persons.addAll(adapter.getPersons());
                else persons.addAll(adapter.search(s.toString()));
                adapter.close();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        adapter.open();
        fullContactsClick();
        persons.clear();
        persons.addAll(adapter.getPersons());
        adapter.close();
        personsAdapter.notifyDataSetChanged();
    }

    public void add(View view) {
        Intent intent = new Intent(this, EditPerson.class);
        startActivity(intent);
    }

    public void fullContactsClick()
    {
        String[] names = {"Иван", "Сергей", "Дмитрий", "Аркадий"};
        String[] families = {"Иванов", "Пеньков", "Синьков", "Глушков"};
        String[] patronymics = {"Максимович","Павлович","Романович","Степанович"};
        String[] phones = {"98657456787","95635479876","9806548798","94536547878"};
        adapter.open();
        for (int i=0;i<30;i++) {
            int nameRand = (int)(Math.random()*(3+1));
            int famRand = (int)(Math.random()*(3+1));
            int patrRand = (int)(Math.random()*(3+1));
            int phoneRand = (int)(Math.random()*(3+1));
            adapter.insert(new Person(1,names[nameRand],families[famRand],
                    patronymics[patrRand],phones[phoneRand],null));
        }
        persons.clear();
        persons.addAll(adapter.getPersons());
        adapter.close();
        personsAdapter.notifyDataSetChanged();
    }
}