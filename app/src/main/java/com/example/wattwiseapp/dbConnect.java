package com.example.wattwiseapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class dbConnect extends SQLiteOpenHelper {

    // infos do banco de dados
    private static String dbName = "findFriendsManager";
    private static int dbVersion = 6;

    // tabela user
    private static String dbTable = "users";
    private static  String ID = "id";
    private static  String fullname = "fullname";
    private static  String emailAddress = "emailAddress";
    private static  String password = "password";
    private static  String phoneNumber = "phoneNumber";
    private static  String dob = "dob";
    private static  String bio = "bio";

    // tabela comodo
    private static String comodoTable = "room";
    private static  String idComodo = "idComodo";
    private static  String nomeComodo = "nomeComodo";
    private static  String tipoComodo = "tipoComodo";
    private static  String qtdTomadas = "qtdTomadas";
    private static  String descricao = "descricao";

    // tabela eletrodomestico
    private static String eletroTable = "eletro";
    private static  String idEletro = "idEletro";
    private static  String nomeEletro = "nomeEletro";
    private static  String TipoEletro = "tipoEletro";
    private static  String comodoEletro = "comodoEletro";
    private static  String potenciaEletro = "potenciaEletro";
    private static  String descricaoEletro = "descricaoEletro";


    public dbConnect(@Nullable Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // tabela user
        // obs.: SEMPRE coloque IF NOT EXISTS para nao dar conflito
        String query =
                "CREATE TABLE IF NOT EXISTS " + dbTable + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        fullname + " TEXT, " +
                        emailAddress + " TEXT, " +
                        password + " TEXT, " +
                        dob + " TEXT, " +
                        phoneNumber + " TEXT, " +
                        bio + " TEXT" +
                        ")";

        db.execSQL(query);

        // tabela comodo
        String queryComodo =
                "CREATE TABLE IF NOT EXISTS " + comodoTable + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        nomeComodo + " TEXT, " +
                        tipoComodo + " TEXT, " +
                        qtdTomadas + " TEXT, " +
                        descricao + " TEXT" +
                        ")";

        db.execSQL(queryComodo);

        // tabela eletronico
        String queryEletro =
                "CREATE TABLE IF NOT EXISTS " + eletroTable + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        nomeEletro + " TEXT, " +
                        TipoEletro + " TEXT, " +
                        comodoEletro + " TEXT, " +
                        potenciaEletro + " TEXT, " +
                        descricaoEletro + " TEXT " +
                        ")";

        db.execSQL(queryEletro);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // user
        db.execSQL("DROP TABLE IF EXISTS " + dbTable);
        // comodo
        db.execSQL("DROP TABLE IF EXISTS " + comodoTable);
        // eletronico
        db.execSQL("DROP TABLE IF EXISTS " + eletroTable);

        onCreate(db);

    }

    // criar usuario
    public void addUser(Users user){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(fullname,user.getFullname());
        values.put(emailAddress,user.getEmailAddress());
        values.put(password,user.getPassword());
        values.put(phoneNumber,user.getPhoneNumber());
        values.put(dob,user.getDOB());
        values.put(bio,user.getBio());

        db.insert(dbTable, null, values);

    }

    // checar login
    public boolean checkLogin(String emailAddress, String password){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE emailAddress = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[] {emailAddress, password});

        boolean loginSuccess = cursor.moveToFirst();

        cursor.close();
        db.close();

        return loginSuccess;

    }

    // criar comodo
    public void addComodo(Room room){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(nomeComodo,room.getNomeComodo());
        values.put(tipoComodo,room.getTipoComodo());
        values.put(qtdTomadas,room.getQtdTomadas());
        values.put(descricao,room.getDescricao());

        db.insert(comodoTable, null, values);

    }

    // criar eletronico
    public void addAppliance(Appliance appliance){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(nomeEletro,appliance.getNomeEletro());
        values.put(TipoEletro,appliance.getTipoEletro());
        values.put(comodoEletro,appliance.getComodoEletro());
        values.put(potenciaEletro,appliance.getPotenciaEletro());
        values.put(descricaoEletro,appliance.getDescricaoEletro());

        db.insert(eletroTable, null, values);

    }

}
