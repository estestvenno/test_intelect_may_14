package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Config {
    public static GameTextureManager GameTexManager= new GameTextureManager();
    public static int MusicVolume = 50;
    public static FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    public static FreeTypeFontGenerator generator;
    // Основные настройки
    Config(){
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Kurland.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" + FreeTypeFontGenerator.DEFAULT_CHARS;
    }
}
