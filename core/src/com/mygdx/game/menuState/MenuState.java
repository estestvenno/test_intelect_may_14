package com.mygdx.game.menuState;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.mygdx.game.Config.GameTexManager;
import static com.mygdx.game.Config.MusicVolume;
import static com.mygdx.game.Config.generator;
import static com.mygdx.game.Config.parameter;
import static com.mygdx.game.basicState.gameConfig.GlobalMapSIZE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.basicState.BasicState;
import com.mygdx.game.basicState.Map.GlobalHexagonMap;
import com.mygdx.game.basicState.Map.GlobalHexagonalCell;
import com.mygdx.game.basicState.gameConfig;
import com.mygdx.game.state.GameStateManager;
import com.mygdx.game.state.State;

public class MenuState extends State {
    private final GlobalHexagonMap GHM;
    private final SpriteBatch batch;
    private int seed;
    private Texture backgroundTexture;
    private Array<GlobalHexagonalCell> sortedCell;
    private int hexRadius = 128;
    private float cameraMoveTimer;
    private float cameraMoveTime;
    private float cameraSpeed;
    private Vector2 cameraDirection;


    public MenuState(GameStateManager gsm) {
        super(gsm);

        this.gsm = gsm;
        this.stage = new Stage();
        this.batch = new SpriteBatch();
        backgroundTexture = GameTexManager.selectionTexturesForMenu("background");
        System.out.println(backgroundTexture);

        setCamera();


        seed = random.nextInt(100000) + 1;


        GHM = new GlobalHexagonMap(hexRadius);
        sortedCell = new Array<GlobalHexagonalCell>();
        cellManager();
        makingDesignMenu();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void ReturningBack() {
        Gdx.input.setInputProcessor(stage);
    }

    public void setCamera() {
        //создаем камеру
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom += 3; // настраиваем стартовый зум
        float initialCameraX = camera.viewportWidth * camera.zoom * 0.5f + hexRadius;
        float initialCameraY = camera.viewportHeight * camera.zoom * 0.5f + hexRadius;
        camera.position.set(initialCameraX, initialCameraY, 0);
        camera.update();

        cameraSpeed = 100.0f; // Скорость перемещения камеры
        cameraMoveTime = 100.0f; // Время перемещения камеры
        cameraMoveTimer = 0.0f; // Текущее время перемещения
        cameraDirection = new Vector2(MathUtils.random(-1.0f, 1.0f), MathUtils.random(-1.0f, 1.0f)).nor();
    }

    public void movingTheCamera(float deltaX, float deltaY) {
        float minX = camera.viewportWidth * camera.zoom * 0.5f + hexRadius;
        float minY = camera.viewportHeight * camera.zoom * 0.5f + hexRadius * 0.5f * 1.2f;

        float maxX = hexRadius * 3 * GlobalMapSIZE * 0.5f - minX;
        float maxY = hexRadius * GlobalMapSIZE - minY;

        float newX = camera.position.x - deltaX * camera.zoom;
        float newY = camera.position.y + deltaY * camera.zoom;
        newX = MathUtils.clamp(newX, minX, maxX);
        newY = MathUtils.clamp(newY, minY, maxY);
        if (newX == minX || newX == maxX || newY == minY || newY == maxY) {
            cameraDirection.set(MathUtils.random(-1.0f, 1.0f), MathUtils.random(-1.0f, 1.0f)).nor();
            cameraMoveTimer = 0.0f;
        }
        camera.position.set(newX, newY, 0);
        camera.update();

        cellManager();
    }

    public void cellManager() {
        sortedCell.clear();

        Vector3 worldCoordinates = new Vector3(0, 0, 0);
        camera.unproject(worldCoordinates);

        int minX = (int) (worldCoordinates.x / (hexRadius * 1.5f));
        int minY = (int) ((worldCoordinates.y - camera.viewportHeight * camera.zoom) / (hexRadius));

        int maxX = (int) ((worldCoordinates.x + camera.viewportWidth * camera.zoom) / (hexRadius * 1.5f));
        int maxY = (int) (worldCoordinates.y / (hexRadius));

        // циклы отрисовки
        for (int i = minY - 2; i < maxY + 2; i++) {
            for (int j = minX - 2; j < maxX + 2; j++) {
                if (j >= 0 && j < GlobalMapSIZE && i >= 0 && i < GlobalMapSIZE) {
                    sortedCell.add(GHM.HexagonMap[i][j]);
                }
            }
        }
        sortedCell.sort((actor1, actor2) -> {
            int priorityComparison = Float.compare(actor2.getPriority(), actor1.getPriority());
            if (priorityComparison != 0) {
                return priorityComparison;
            } else {
                return Float.compare(actor2.getY(), actor1.getY());
            }
        });
    }

    public void makingDesignMenu() {
        stage.clear();
        standardizedPart(1);
    }

    public void makingDesignNewGame() {
        stage.clear();
        float bth_width = Gdx.graphics.getWidth() / 4f; // Ширина кнопки
        float bth_height = Gdx.graphics.getHeight() / 6.4f; // Высота
        float padding = Gdx.graphics.getHeight() / 6f; // Отступ




        // табличиччка для разных штучек
        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Slide"))));
        table.setPosition(bth_width + bth_width / 4, padding * 0.25f + bth_height);
        table.setSize(Gdx.graphics.getWidth() - (bth_width + bth_width / 8 * 3), padding * (3 + 0.25f) + bth_height - padding * 0.25f);
        stage.addActor(table);

        // делаем шрифты
        parameter.size = 44;
        BitmapFont font = generator.generateFont(parameter);
        parameter.size = 34;
        BitmapFont fontSettings = generator.generateFont(parameter);

        // стандартная подпись
        Label.LabelStyle styleL = new Label.LabelStyle();
        styleL.font = font;
        Label label = new Label("Новая игра", styleL);
        label.setSize(table.getWidth() / 4, table.getHeight() / 10);
        label.setPosition(table.getWidth() / 2 - label.getWidth() / 2, table.getHeight() / 10 * 7.75f);
        label.setAlignment(Align.center, Align.center);
        table.addActor(label);

        // подпись сид
        styleL.font = fontSettings;
        label = new Label("Сид", styleL);
        label.setSize(table.getWidth() / 4, table.getHeight() / 10);
        label.setPosition(table.getWidth() / 4 - label.getWidth() / 2, table.getHeight() / 10 * 6f);
        label.setAlignment(Align.center, Align.center);
        table.addActor(label);

        // ввод сид
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = fontSettings; // Установка шрифта
        style.fontColor = Color.WHITE; // Установка цвета шрифта
        style.background = new NinePatchDrawable(new NinePatch(GameTexManager.getTexture("variation01"), 10, 10, 0, 0));
        TextField textField = new TextField(String.valueOf(seed), style);
        textField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                // Разрешить ввод только цифр и ограничить количество символов до 5
                return Character.isDigit(c) && textField.getText().length() < 8;
            }
        });
        textField.setSize(table.getWidth() / 2, table.getHeight() / 10);
        textField.setPosition(table.getWidth() / 2 - label.getWidth() / 2, table.getHeight() / 10 * 6f);
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                // Обработка отрисовки текста с отступами
                if (textField.getText().length() < 9) {
                    textField.setText(textField.getText());
                }
            }
        });
        table.addActor(textField);

        // кнопочка случайного сида
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Cell01")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Cell02")));

        ImageButton Ibutton = new ImageButton(imageButtonStyle);
        Ibutton.setPosition(table.getWidth() / 16, table.getHeight() / 10 * 6);
        Ibutton.setSize(table.getWidth() / 8, table.getHeight() / 10);

        Ibutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                seed = random.nextInt(100000);
                textField.setText(String.valueOf(seed));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        seed = random.nextInt(100000);
                        textField.setText(String.valueOf(seed));
                    }
                }, 0, 1 / 10f);

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Timer.instance().clear();
            }
        });
        table.addActor(Ibutton);

        // кнопочка запуска игры
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Play col_Button")));
        textButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Play Button")));

        TextButton button = new TextButton("", textButtonStyle);
        button.setSize(bth_width, bth_height);
        button.setPosition(table.getWidth() / 2 - button.getWidth() / 2, table.getHeight() / 10);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (textField.getText().length() != 0){gameConfig.setGlobalMapSEED(Integer.parseInt(textField.getText()));}
                gameConfig.setGlobalMapSIZE(512);
                gsm.push(new BasicState(gsm, false));
            }
        });
        table.addActor(button);



        standardizedPart(2);
    }

    public void makingDesignSettings() {
        stage.clear();
        float bth_width = Gdx.graphics.getWidth() / 4f; // Ширина кнопки
        float bth_height = Gdx.graphics.getHeight() / 6.4f; // Высота
        float padding = Gdx.graphics.getHeight() / 6f; // Отступ

        //заранее создадим шрифт
        parameter.size = 44;
        BitmapFont font = generator.generateFont(parameter);


        // табличиччка для разных штучек
        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Slide"))));

        table.setPosition(bth_width + bth_width / 4, padding * 0.25f + bth_height);
        table.setSize(Gdx.graphics.getWidth() - (bth_width + bth_width / 8 * 3), padding * (3 + 0.25f) + bth_height - padding * 0.25f);

        stage.addActor(table);

        // заполняем таблицу
        // подпись "звук"
        Label.LabelStyle styleL = new Label.LabelStyle();
        styleL.font = font;
        Label label = new Label("Звук", styleL);
        label.setSize(table.getWidth() / 4, table.getHeight() / 10);
        label.setPosition(table.getWidth() / 2 - label.getWidth() / 2, table.getHeight() / 10 * 6);
        label.setAlignment(Align.center, Align.center);
        table.addActor(label);

        label = new Label("Настройки", styleL);
        label.setSize(table.getWidth() / 4, table.getHeight() / 10);
        label.setPosition(table.getWidth() / 2 - label.getWidth() / 2, table.getHeight() / 10 * 7.75f);
        label.setAlignment(Align.center, Align.center);
        table.addActor(label);

        // Создание текстуры для заполненной части прогресс-бара
        TextureRegionDrawable fillDrawable = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("full")));
        TextureRegionDrawable emptyDrawable = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("not_full")));
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = emptyDrawable;
        style.knobBefore = fillDrawable;
        ProgressBar progressBar = new ProgressBar(0, 100, 1, false, style);
        progressBar.setSize(table.getWidth() / 2, table.getHeight() / 10);
        progressBar.setPosition(table.getWidth() / 4, table.getHeight() / 10 * 5);
        progressBar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = (int) progressBar.getValue();
                System.out.println(value);
            }
        });
        progressBar.setValue(MusicVolume);
        table.addActor(progressBar);

        // кнопочки для progressBar -
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("LCell01")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("LCell02")));
        ImageButton button = new ImageButton(imageButtonStyle);
        button.setPosition(table.getWidth() / 16, table.getHeight() / 10 * 5);
        button.setSize(table.getWidth() / 8, table.getHeight() / 10);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                progressBar.setValue(progressBar.getValue() - 1);
                MusicVolume--;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        progressBar.setValue(progressBar.getValue() - 1);
                        MusicVolume--;
                    }
                }, 0, 1 / 10f);

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Timer.instance().clear();
            }
        });
        table.addActor(button);

        // кнопочки для progressBar +
        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("RCell01")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("RCell02")));
        button = new ImageButton(imageButtonStyle);
        button.setPosition(table.getWidth() / 16 * 13, table.getHeight() / 10 * 5);
        button.setSize(table.getWidth() / 8, table.getHeight() / 10);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                progressBar.setValue(progressBar.getValue() + 1);
                MusicVolume++;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        progressBar.setValue(progressBar.getValue() + 1);
                        MusicVolume++;
                    }
                }, 0, 1 / 10f);

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Timer.instance().clear();
            }
        });
        table.addActor(button);


        // Крестик для закрытия таблички
        standardizedPart(2);
    }

    public void standardizedPart(int degreeOfStandardization) {
        //заранее создадим шрифт
        BitmapFont font = new BitmapFont();

        float bth_width = Gdx.graphics.getWidth() / 4f; // Ширина кнопки
        float bth_height = Gdx.graphics.getHeight() / 6.4f; // Высота
        float padding = Gdx.graphics.getHeight() / 6f; // Отступ

        String[] ListButtonsDown = {"Quit Button", "Settings Button", "Load Button", "New game Button"};
        String[] ListButtons = {"Quit col_Button", "Settings col_Button", "Load col_Button", "New game col_Button"};

        for (int i = 0; i < 4; i++) {
            TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
            textButtonStyle.font = font;
            textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture(ListButtons[i])));
            textButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture(ListButtonsDown[i])));

            TextButton button = new TextButton("", textButtonStyle);
            button.setPosition(bth_width / 8, padding * (i + 0.25f) + bth_height);
            button.setSize(bth_width, bth_height);
            button.setUserObject(ListButtonsDown[i]);

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String buttonTag = (String) event.getListenerActor().getUserObject();
                    if ("New game Button".equals(buttonTag)) {
                        makingDesignNewGame();
                    } else if ("Load Button".equals(buttonTag)) {

                    } else if ("Settings Button".equals(buttonTag)) {
                        makingDesignSettings();
                    } else if ("Quit Button".equals(buttonTag)) {
                        Gdx.app.exit();
                    }
                }
            });
            stage.addActor(button);
        }

        // отбрасываем стандартизируванную част №1
        if (degreeOfStandardization == 1) {
            return;
        }

        // Крестик для закрытия таблички
        ImageButton.ImageButtonStyle crossStyle = new ImageButton.ImageButtonStyle();
        crossStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("03")));
        crossStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("03_d")));
        ImageButton button = new ImageButton(crossStyle);
        button.setSize(button.getWidth(), button.getHeight());
        button.setPosition(Gdx.graphics.getWidth() - bth_width / 8 - button.getWidth() / 1.5f, Gdx.graphics.getHeight() - bth_height - button.getHeight() / 1.5f);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                makingDesignMenu();
            }
        });
        stage.addActor(button);
    }

    public void makingDesignInfo(){
        float bth_width = Gdx.graphics.getWidth() / 4f; // Ширина кнопки
        float bth_height = Gdx.graphics.getHeight() / 6.4f; // Высота
        float padding = Gdx.graphics.getHeight() / 6f; // Отступ

        // табличиччка для разных штучек
        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Slide"))));
        table.setPosition(bth_width + bth_width / 4, padding * 0.25f + bth_height);
        table.setSize(Gdx.graphics.getWidth() - (bth_width + bth_width / 8 * 3), padding * (3 + 0.25f) + bth_height - padding * 0.25f);
        stage.addActor(table);

        // основная инфа об игре
        parameter.size = 44;
        BitmapFont font = generator.generateFont(parameter);

        Label.LabelStyle styleL = new Label.LabelStyle();
        styleL.font = font;
        Label label = new Label("О нас", styleL);
        label.setSize(table.getWidth() / 4, table.getHeight() / 10);
        label.setPosition(table.getWidth() / 2 - label.getWidth() / 2, table.getHeight() / 10 * 7.75f);
        label.setAlignment(Align.center, Align.center);
        table.addActor(label);


        // Крестик для закрытия таблички
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("03")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("03_d")));
        ImageButton button = new ImageButton(imageButtonStyle);
        button.setSize(button.getWidth(), button.getHeight());
        button.setPosition(Gdx.graphics.getWidth() - bth_width / 8 - button.getWidth() / 1.5f, Gdx.graphics.getHeight() - bth_height - button.getHeight() / 1.5f);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                makingDesignMenu();
            }
        });
        stage.addActor(button);
    }

    @Override
    protected void handleInput() {
    }

    @Override
    public void update(float dt) {
        cameraMoveTimer += dt;
        if (cameraMoveTimer < cameraMoveTime) {
            float distance = cameraSpeed * dt;
            movingTheCamera(cameraDirection.x * distance - cameraDirection.x, cameraDirection.y * distance - cameraDirection.y);
        } else {
            cameraDirection.set(MathUtils.random(-1.0f, 1.0f), MathUtils.random(-1.0f, 1.0f)).nor();
            cameraMoveTimer = 0.0f;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (GlobalHexagonalCell Cell : sortedCell) {
            Cell.drawB(batch);
        }
        for (GlobalHexagonalCell Cell : sortedCell) {
            Cell.drawD(batch);
        }
        batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
    }
}

