package com.example.contactbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PersonAdapter extends ArrayAdapter<Person> {
    private final LayoutInflater inflater;
    private final int layout;
    private final List<Person> persons;

    public PersonAdapter(Context context, int resource, List<Person> persons) {
        super(context, resource, persons);
        this.persons = persons;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder") View view=inflater.inflate(this.layout, parent, false);

        TextView nameView = view.findViewById(R.id.nameField);
        TextView familyView = view.findViewById(R.id.familyField);
        TextView patronymicView = view.findViewById(R.id.patronymicField);

        Person person = persons.get(position);

        patronymicView.setText(person.getPatronymic());
        nameView.setText(person.getName());
        familyView.setText(person.getSurname());

        return view;
    }
}

