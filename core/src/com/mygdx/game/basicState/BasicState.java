package com.mygdx.game.basicState;

import static com.mygdx.game.Config.GameTexManager;
import static com.mygdx.game.Config.MusicVolume;
import static com.mygdx.game.Config.generator;
import static com.mygdx.game.Config.parameter;
import static com.mygdx.game.basicState.gameConfig.GlobalMapSEED;
import static com.mygdx.game.basicState.gameConfig.GlobalMapSIZE;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.GameTextureManager;
import com.mygdx.game.basicState.City.CityManager;
import com.mygdx.game.basicState.City.CitySystem.LifeSupportSystem;
import com.mygdx.game.basicState.City.CitySystem.LifeSupportSystem1;
import com.mygdx.game.basicState.City.CitySystem.TradingSystem1;
import com.mygdx.game.basicState.City.Component.BuildingsComponent;
import com.mygdx.game.basicState.City.Component.CityComponent;
import com.mygdx.game.basicState.City.Component.InventoryComponent;
import com.mygdx.game.basicState.Map.GlobalHexagonMap;
import com.mygdx.game.basicState.Map.GlobalHexagonalCell;
import com.mygdx.game.basicState.Player.Player;
import com.mygdx.game.basicState.Type.BuildingsType;
import com.mygdx.game.basicState.Type.ResourceType;
import com.mygdx.game.basicState.pathFinder.pathManager;
import com.mygdx.game.menuState.MenuState;
import com.mygdx.game.state.GameStateManager;
import com.mygdx.game.state.State;

import java.util.HashMap;
import java.util.Map;

public class BasicState extends State {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;

    private final int hexRadius = 128;
    private Player player;
    private GlobalHexagonMap GHM;
    private CityManager CM;
    private Array<GlobalHexagonalCell> sortedCell;
    private Engine engine;
    private pathManager PM;
    private int ParameterFlag = 0;
    private boolean pauseFlag = true;
    private boolean loadingFlag = true;
    private float stateTime;

    public BasicState(GameStateManager gsm, boolean saving) {
        super(gsm);
        if (saving) {
        }
        System.out.println(GlobalMapSEED);


        //создаем все необходимое для работы
        this.gsm = gsm;
        this.stage = new Stage();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        //создаем камеру
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        engine = new Engine();


        new Thread(() -> {
            // основной класс карты
            TradingSystem1 tradingSystem1 = new TradingSystem1();
            GHM = new GlobalHexagonMap(hexRadius);
            CM = new CityManager(GHM, engine, tradingSystem1);
            PM = pathManager.createPathManagerByMap(GHM.HexagonMap);
            PM.LookAWayACity(CM.listCity);
            GHM.reloadingTheRoad(CM, PM);


//            engine.addSystem(new LifeSupportSystem1(5, GHM, CM));

            //нужно для оптимизации игры
            sortedCell = new Array<GlobalHexagonalCell>();
            cellManager();

            //запускаем прием жестов
            InputMultiplexer inputMultiplexer = new InputMultiplexer();
            inputMultiplexer.addProcessor(stage); // Обработчик ввода для сцены (кнопка)
            inputMultiplexer.addProcessor(new GestureDetector(new MyGestureListener())); // Обработчик жестов

            // Устанавливаем InputMultiplexer как текущий обработчик ввода
            Gdx.input.setInputProcessor(inputMultiplexer);


            // настройки камеры
//            camera.zoom += 4; // настраиваем стартовый зум
            float initialCameraX = camera.viewportWidth * camera.zoom * 0.5f + hexRadius;
            float initialCameraY = camera.viewportHeight * camera.zoom * 0.5f + hexRadius;
            camera.position.set(initialCameraX, initialCameraY, 0);
            camera.update();

            loadingFlag = false;
            zeroInterface();

            player = new Player(PM, GHM, CM);

        }).start();
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

    public void zeroInterface() {
        stage.clear();
        ParameterFlag = 0;

        float Size = (float) Gdx.graphics.getHeight() / 8;
        float padding = Gdx.graphics.getHeight() / 64f;


        // кнопочки для меню
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Menu col_Button")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Menu")));
        ImageButton button = new ImageButton(imageButtonStyle);
        button.setSize(Size, Size);
        button.setPosition(padding, Gdx.graphics.getHeight() - padding - Size);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inGameMenu();
            }
        });
        stage.addActor(button);

        // кнопочки управления для игрока "маркер"
        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Marker col_Square")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Marker Square")));
        button = new ImageButton(imageButtonStyle);
        button.setSize(Size, Size);
        button.setPosition(Gdx.graphics.getWidth() - padding - Size, Gdx.graphics.getHeight() - padding - Size);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int[] InMatrix = player.GoToCoordinates();
                float coordinates_x = GHM.HexagonMap[InMatrix[0]][InMatrix[1]].getX();
                float coordinates_y = GHM.HexagonMap[InMatrix[0]][InMatrix[1]].getY();
                moveToCoordinates(coordinates_x, coordinates_y);
            }
        });
        stage.addActor(button);

        // кнопочки управления для игрока "город"
        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("City col_Square")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("City Square")));
        button = new ImageButton(imageButtonStyle);
        button.setSize(Size, Size);
        button.setPosition(Gdx.graphics.getWidth() - padding - Size, Gdx.graphics.getHeight() - 2 * (padding + Size));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ParameterFlag = (ParameterFlag == 5) ? 0 : 5;
                if (ParameterFlag == 0) {
                    zeroInterface();
                } else {
                    makingDesignInventory();
                }
            }
        });
        stage.addActor(button);

        // кнопочки управления для игрока "Стройка"
        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Hammer col_Square")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Hammer Square")));
        button = new ImageButton(imageButtonStyle);
        button.setSize(Size, Size);
        button.setPosition(Gdx.graphics.getWidth() - padding - Size, Gdx.graphics.getHeight() - 3 * (padding + Size));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });
        stage.addActor(button);

        // кнопочки управления для игрока "Торговля"
        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Trading col_Square")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Trading Square")));
        button = new ImageButton(imageButtonStyle);
        button.setSize(Size, Size);
        button.setPosition(Gdx.graphics.getWidth() - padding - Size, Gdx.graphics.getHeight() - 4 * (padding + Size));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });
        stage.addActor(button);

        // кнопочки управления для игрока "Торговля"
        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Army col_Square")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Army Square")));
        button = new ImageButton(imageButtonStyle);
        button.setSize(Size, Size);
        button.setPosition(Gdx.graphics.getWidth() - padding - Size, Gdx.graphics.getHeight() - 5 * (padding + Size));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });
        stage.addActor(button);

        // кнопочки управления для игрока "Рабочие"
        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Worker col_Square")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Worker Square")));
        button = new ImageButton(imageButtonStyle);
        button.setSize(Size, Size);
        button.setPosition(Gdx.graphics.getWidth() - padding - Size, Gdx.graphics.getHeight() - 6 * (padding + Size));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ParameterFlag = (ParameterFlag == 9) ? 0 : 9;
                if (ParameterFlag == 0) {
                    zeroInterface();
                    HashMap<BuildingsType, Array<Vector2>> Building = player.PlayersCity.getComponent(BuildingsComponent.class).Buildings;
                    for (Map.Entry<BuildingsType, Array<Vector2>> entry : Building.entrySet()) {
                        if (entry.getValue().size == 0) {
                            continue;
                        }

                        for (int k = 0; k < entry.getValue().size; k++) {
                            int x_ = (int) entry.getValue().get(k).x;
                            int y_ = (int) entry.getValue().get(k).y;

                            GHM.HexagonMap[x_][y_].informationAboutView = 0;
                            GHM.HexagonMap[x_][y_].changTexture();
                        }
                    }
                    cellManager();
                } else {
                    makingDesignJobs();
                }
            }
        });
        stage.addActor(button);

        // кнопочка для паузы
        changingPause();
    }

    public void inGameMenu() {
        stage.clear();
        ParameterFlag = 0;
        standardizedPart();
        changingPause();
    }

    public void standardizedPart() {
        stage.clear();

        //заранее создадим шрифт
        float Size = (float) Gdx.graphics.getHeight() / 8;
        float padding = Gdx.graphics.getHeight() / 64f;

        String[] ListButtonsDown = {"Menu", "Load Square", "Save Square", "Settings Square", "Home Square",};
        String[] ListButtons = {"Menu col_Button", "Load col_Square", "Save col_Square", "Settings col_Square", "Home col_Square",};

        for (int i = 0; i < ListButtons.length; i++) {
            ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
            imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture(ListButtons[i])));
            imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture(ListButtonsDown[i])));

            ImageButton button = new ImageButton(imageButtonStyle);
            button.setPosition(padding, Gdx.graphics.getHeight() - (Size + padding) * (i + 1));
            button.setSize(Size, Size);
            button.setUserObject(ListButtonsDown[i]);

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String buttonTag = (String) event.getListenerActor().getUserObject();

                    switch (buttonTag) {
                        case "Menu":
                            zeroInterface();
                            break;
                        case "Load Square":
                            break;
                        case "Save Square":
                            break;

                        case "Settings Square":
                            ParameterFlag = (ParameterFlag == 4) ? 0 : 4;
                            if (ParameterFlag == 0) {
                                inGameMenu();
                            } else {
                                makingDesignSettings();
                            }
                            break;

                        case "Home Square":
                            gsm.pop();
                            break;
                    }
                }
            });
            stage.addActor(button);
        }
    }

    public void changingPause() {

        float Size = (float) Gdx.graphics.getHeight() / 8;
        float padding = Gdx.graphics.getHeight() / 64f;

        // кнопочка для паузы
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        if (pauseFlag) {
            imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Pause/Play col_Button")));
            imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Pause/Play")));
        } else {
            imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Play/Pause col_Button")));
            imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Play/Pause")));
        }

        ImageButton button = new ImageButton(imageButtonStyle);
        button.setSize(Size, Size);
        button.setPosition(padding * 2 + Size, Gdx.graphics.getHeight() - Size - padding);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseFlag = !pauseFlag;
                changingPause();
                button.remove();
            }
        });
        stage.addActor(button);
    }

    public void makingDesignSettings() {
        float bth_width = Gdx.graphics.getWidth() / 4f; // Ширина кнопки
        float bth_height = Gdx.graphics.getHeight() / 6.4f; // Высота
        float padding = Gdx.graphics.getHeight() / 6f; // Отступ

        // табличиччка для разных штучек
        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("Slide"))));
        table.setPosition(bth_width + bth_width / 4, padding * 0.25f + bth_height);
        table.setSize(Gdx.graphics.getWidth() - (bth_width + bth_width / 8 * 3), padding * (3 + 0.25f) + bth_height - padding * 0.25f);
        stage.addActor(table);

        // подпись "звук"
        parameter.size = 44;
        BitmapFont font = generator.generateFont(parameter);

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
        progressBar.setPosition((table.getWidth() - progressBar.getWidth()) / 2, table.getHeight() / 10 * 5);
        progressBar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = (int) progressBar.getValue();
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
        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("03")));
        imageButtonStyle.down = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("03_d")));
        button = new ImageButton(imageButtonStyle);
        button.setSize(button.getWidth(), button.getHeight());
        button.setPosition(Gdx.graphics.getWidth() - bth_width / 8 - button.getWidth() / 1.5f, Gdx.graphics.getHeight() - bth_height - button.getHeight() / 1.5f);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inGameMenu();
            }
        });
        stage.addActor(button);


    }

    public void moveToCoordinates(float targetX, float targetY) {
        float minX = camera.viewportWidth * camera.zoom * 0.5f + hexRadius;
        float minY = camera.viewportHeight * camera.zoom * 0.5f + hexRadius * 0.5f * 1.2f;

        float maxX = hexRadius * 3 * GlobalMapSIZE * 0.5f - minX;
        float maxY = hexRadius * GlobalMapSIZE - minY;

        float newX = MathUtils.clamp(targetX, minX, maxX);
        float newY = MathUtils.clamp(targetY, minY, maxY);

        camera.position.set(newX, newY, 0);
        camera.update();

        cellManager();
    }

    public void makingDesignInventory() {
        ParameterFlag = 5;

        float Size = (float) Gdx.graphics.getHeight() / 8;
        float padding = Gdx.graphics.getHeight() / 64f;

        // табличиччка для разных штучек
        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("SlideInventory"))));
        stage.addActor(table);

        // цикл инвентаря
        HashMap<ResourceType, Float> profit_per_turn = player.ProfitPerTurn();
        Map<ResourceType, Float> inventory = player.PlayersCity.getComponent(InventoryComponent.class).Inventory;

        // шрифт
        parameter.size = 30;
        BitmapFont font = generator.generateFont(parameter);


        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;


        Label label1 = new Label("Ресурс", labelStyle);
        Label label2 = new Label("Количество", labelStyle);
        Label label3 = new Label("Доход", labelStyle);

        Table tableIn = new Table();
        table.add(label1).size(Size, Size).pad(Size);
        table.add(label2).size(Size, Size).pad(Size);
        table.add(label3).size(Size, Size).pad(Size);
        table.add(tableIn).row();

        for (ResourceType resourceType : ResourceType.values()) {
            Image image = new Image(GameTexManager.gettingAResource(resourceType));
            image.setSize(Size, Size);
            label1 = new Label(inventory.get(resourceType).toString(), labelStyle);
            label2 = new Label(profit_per_turn.get(resourceType).toString(), labelStyle);

            tableIn = new Table();
            table.add(image).size(Size, Size).pad(Size);
            table.add(label1).size(Size, Size).pad(Size);
            table.add(label2).size(Size, Size).pad(Size);
            table.add(tableIn).row();
        }


        // Создайте ScrollPaneStyle и добавьте его в skin
        // Задайте максимальную высоту ползунка
        float maxKnobHeight = 50;

        // Создайте обрезанную текстуру для ползунка с ограниченной высотой
        Texture knobTexture = GameTexManager.gettingAResource(ResourceType.COINS);
        TextureRegion region = new TextureRegionDrawable(new TextureRegion(knobTexture)).getRegion();
        TextureRegionDrawable limitedKnobDrawable = new TextureRegionDrawable(new TextureRegion(region, region.getRegionX(), region.getRegionY(), (int) maxKnobHeight, (int) maxKnobHeight));

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = limitedKnobDrawable;
        scrollPaneStyle.vScroll = new TextureRegionDrawable(new TextureRegion(GameTexManager.getTexture("")));

        ScrollPane scrollPane = new ScrollPane(table, scrollPaneStyle);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFillParent(false);
        scrollPane.setSize(8 * (Size + padding), Gdx.graphics.getHeight() - 2 * (Size + padding));
        scrollPane.setPosition(3 * (Size + padding), (Size + padding));
        stage.addActor(scrollPane);


    }

    public void makingDesignJobs() {
        ParameterFlag = 9;

        // шрифт
        parameter.size = 30;
        BitmapFont font = generator.generateFont(parameter);


        HashMap<BuildingsType, Integer> Jobs = player.PlayersCity.getComponent(BuildingsComponent.class).getNumberOfJobs();
        HashMap<BuildingsType, Array<Vector2>> Building = player.PlayersCity.getComponent(BuildingsComponent.class).Buildings;
        HashMap<BuildingsType, Integer> Workers = player.Workers;
        for (Map.Entry<BuildingsType, Array<Vector2>> entry : Building.entrySet()) {
            if (entry.getValue().size == 0) {
                continue;
            }

            if (!Workers.containsKey(entry.getKey())) {
                for (int k = 0; k < Jobs.get(entry.getKey()); k++) {
                    int x = (int) entry.getValue().get(k).x;
                    int y = (int) entry.getValue().get(k).y;

                    GHM.HexagonMap[x][y].informationAboutView = 2;
                    GHM.HexagonMap[x][y].changTexture();
                }
                continue;
            }

            for (int k = 0; k < Workers.get(entry.getKey()); k++) {
                int x = (int) entry.getValue().get(k).x;
                int y = (int) entry.getValue().get(k).y;

                GHM.HexagonMap[x][y].informationAboutView = 1;
                GHM.HexagonMap[x][y].changTexture();
            }

            for (int k = Workers.get(entry.getKey()); k < Workers.get(entry.getKey()) + Jobs.get(entry.getKey()); k++) {
                int x = (int) entry.getValue().get(k).x;
                int y = (int) entry.getValue().get(k).y;

                GHM.HexagonMap[x][y].informationAboutView = 2;
                GHM.HexagonMap[x][y].changTexture();
            }
        }
        cellManager();
    }

    public void addingWorker(int x, int y) {
        HashMap<BuildingsType, Integer> Workers = player.Workers;

        int numberOfJobs = 0;
        for (Map.Entry<BuildingsType, Integer> entry : Workers.entrySet()) {
            numberOfJobs += entry.getValue();
        }

        if (numberOfJobs >= player.PlayersCity.getComponent(CityComponent.class).populationSize) {
            return;
        }

        System.out.println("__________________1");
        if (GHM.HexagonMap[x][y].informationAboutView == 2) {
            BuildingsType build = GHM.HexagonMap[x][y].informationAboutBuilding;
            if (Workers.containsKey(build)) {
                player.Workers.put(build, Workers.get(build) + 1);

            } else {
                player.Workers.put(build, 1);
            }
            GHM.HexagonMap[x][y].informationAboutView = 1;
        }
    }

    @Override
    public void ReturningBack() {
    }

    @Override
    protected void handleInput() {
    }

    @Override
    public void update(float dt) {
        if (pauseFlag && ParameterFlag == 0) {
            engine.update(dt);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0.86f, 0.82f, 0.75f, 1);
        stateTime += Gdx.graphics.getDeltaTime();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (loadingFlag) {
            TextureRegion currentFrame = GameTexManager.getGif("load_gif").getKeyFrame(stateTime);
            Texture texture = GameTexManager.getTexture("logo");
            float width = (float) Gdx.graphics.getHeight() / 6;
            float height = (float) Gdx.graphics.getHeight() / 6;
            float x = (Gdx.graphics.getWidth() - width) / 2;

            batch.draw(currentFrame, x, height, width, height);
            batch.draw(texture, (Gdx.graphics.getWidth() - width * 4) / 2, height * 2, width * 4, height * 4);
            batch.end();
            return;
        }

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
    }

    private class MyGestureListener extends GestureDetector.GestureAdapter {
        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            if (ParameterFlag != 0 && ParameterFlag != 9) {
                return true;
            }

            float minX = camera.viewportWidth * camera.zoom * 0.5f + hexRadius;
            float minY = camera.viewportHeight * camera.zoom * 0.5f + hexRadius * 0.5f * 1.2f;

            float maxX = hexRadius * 3 * GlobalMapSIZE * 0.5f - minX;
            float maxY = hexRadius * GlobalMapSIZE - minY;

            float newX = camera.position.x - deltaX * camera.zoom;
            float newY = camera.position.y + deltaY * camera.zoom;
            newX = MathUtils.clamp(newX, minX, maxX);
            newY = MathUtils.clamp(newY, minY, maxY);
            camera.position.set(newX, newY, 0);
            camera.update();

            cellManager();

            return true;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            if (ParameterFlag < 9) {
                return true;
            }

            Vector3 worldCoordinates = new Vector3(x, y, 0);
            camera.unproject(worldCoordinates);
            float offsetX = worldCoordinates.x;
            float offsetY = worldCoordinates.y;
            int hexD = hexRadius * 2;

            float gridW = 0.75f * hexD;
            float gridH = 0.5f * hexD;
            float halfH = 0.5f * 0.5f * hexD;

            int globalX = (int) (offsetX / gridW);
            int globalY;

            boolean globalXIsOdd = globalX % 2 == 1;

            if (globalXIsOdd) {
                globalY = (int) ((offsetY - halfH) / gridH);
            } else {
                globalY = (int) (offsetY / gridH);
            }

            double relX = offsetX - (globalX * gridW);
            double relY;

            if (globalXIsOdd) {
                relY = (offsetY - (globalY * gridH)) - halfH;
            } else {
                relY = offsetY - (globalY * gridH);
            }

            float c = 0.25f * hexD;
            float m = c / halfH;
            if (relX < (-m * relY) + c) {
                globalX--;
                if (!globalXIsOdd)
                    globalY--;
            } else if (relX < (m * relY) - c) {
                globalX--;
                if (globalXIsOdd)
                    globalY++;
            }
            globalX = MathUtils.clamp(globalX, 0, GlobalMapSIZE - 1);
            globalY = MathUtils.clamp(globalY, 0, GlobalMapSIZE - 1);
            if (ParameterFlag == 9) {
                addingWorker(globalY, globalX);
                GHM.HexagonMap[globalY][globalX].changTexture();
            }
            System.out.println(globalX + " " + globalY);
            return true;
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            if (ParameterFlag != 0) {
                return true;
            }

            float initialDistance = initialPointer1.dst(initialPointer2);
            float currentDistance = pointer1.dst(pointer2);
            float scaleAmount = (currentDistance - initialDistance) * 0.0001f;

            camera.zoom -= scaleAmount;
            camera.zoom = MathUtils.clamp(camera.zoom, 1f, 5f);

            Vector3 worldCoordinates = new Vector3(0, 0, 0);
            camera.unproject(worldCoordinates);

            float minX = camera.viewportWidth * camera.zoom * 0.5f + hexRadius;
            float minY = camera.viewportHeight * camera.zoom * 0.5f + hexRadius * 0.5f;

            float maxX = hexRadius * 3 * GlobalMapSIZE * 0.5f - minX;
            float maxY = hexRadius * GlobalMapSIZE - minY;

            float newX = MathUtils.clamp(camera.position.x, minX, maxX);
            float newY = MathUtils.clamp(camera.position.y, minY, maxY);
            camera.position.set(newX, newY, 0);
            Gdx.app.log("Pinch", "Camera Position: " + camera.position);

            camera.update();
            cellManager();
            return true;
        }
    }
}
