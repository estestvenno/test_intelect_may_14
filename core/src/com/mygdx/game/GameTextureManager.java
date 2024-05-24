package com.mygdx.game;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.basicState.Type.BuildingsType;
import com.mygdx.game.basicState.Type.ResourceType;

import java.util.HashMap;

public class GameTextureManager {
    private final HashMap<String, Texture> textures;
    private HashMap<String, Animation<TextureRegion>> gif;

    public GameTextureManager() {
        textures = new HashMap<>();
        gif = new HashMap<>();
        initializingBaseTextures();

        // пустая текстурка для отображения пустоты
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0); // Transparent
        pixmap.fill();
        textures.put("", new Texture(pixmap));
    }

    public void loadTexture(String key, String path) {
        Texture texture = new Texture(Gdx.files.internal(path), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        textures.put(key, texture);
    }

    public void loadGif(String key, String path) {
        Animation<TextureRegion> gifFrames = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal(path).read());
        gif.put(key, gifFrames);
    }

    public Animation<TextureRegion> getGif(String key) {
        return gif.get(key);
    }

    public Texture getTexture(String key) {
        return textures.get(key);
    }

    public void disposeAllTextures() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();
    }

    public void initializingBaseTextures() {
        // интерфейс
        loadTexture("Menu col_Button", "data_Img/Menu/Square Buttons/Colored Square Buttons/Menu col_Square Button.png");
        loadTexture("Pause/Play col_Button", "data_Img/Menu/Square Buttons/Colored Square Buttons/Pause col_Square Button.png");
        loadTexture("Play/Pause col_Button", "data_Img/Menu/Square Buttons/Colored Square Buttons/Play col_Square Button.png");
        loadTexture("Pause col_Button", "data_Img/Menu/Square Buttons/Colored Square Buttons/Pause col_Square Button.png");

        loadTexture("Menu", "data_Img/Menu/Square Buttons/Square Buttons/Menu Square Button.png");
        loadTexture("Pause/Play", "data_Img/Menu/Square Buttons/Square Buttons/Pause Square Button.png");
        loadTexture("Play/Pause", "data_Img/Menu/Square Buttons/Square Buttons/Play Square Button.png");
        loadTexture("Pause", "data_Img/Menu/Square Buttons/Square Buttons/Pause Square Button.png");

        loadTexture("SlideBar", "data_Img/Menu/MiniPanel04.jpg");

        // меню игры
        loadTexture("Load col_Square", "data_Img/Menu/Square Buttons/Colored Square Buttons/Load col_Square Button.png");
        loadTexture("Save col_Square", "data_Img/Menu/Square Buttons/Colored Square Buttons/Save col_Square Button.png");
        loadTexture("Settings col_Square", "data_Img/Menu/Square Buttons/Colored Square Buttons/Settings col_Square Button.png");
        loadTexture("Info col_Square", "data_Img/Menu/Square Buttons/Colored Square Buttons/Info col_Square Button.png");
        loadTexture("Home col_Square", "data_Img/Menu/Square Buttons/Colored Square Buttons/Home col_Square Button.png");


        loadTexture("Load Square", "data_Img/Menu/Square Buttons/Square Buttons/Load Square Button.png");
        loadTexture("Save Square", "data_Img/Menu/Square Buttons/Square Buttons/Save Square Button.png");
        loadTexture("Settings Square", "data_Img/Menu/Square Buttons/Square Buttons/Settings Square Button.png");
        loadTexture("Info Square", "data_Img/Menu/Square Buttons/Square Buttons/Info Square Button.png");
        loadTexture("Home Square", "data_Img/Menu/Square Buttons/Square Buttons/Home Square Button.png");


        //кнопочки игрока
        loadTexture("City col_Square", "data_Img/Menu/Square Buttons/Colored Square Buttons/City col_Square Button.png");
        loadTexture("Marker col_Square", "data_Img/Menu/Square Buttons/Colored Square Buttons/Marker col_Square Button.png");
        loadTexture("Hammer col_Square", "data_Img/Menu/Square Buttons/Colored Square Buttons/Hammer col_Square Button.png");
        loadTexture("Trading col_Square", "data_Img/Menu/Square Buttons/Colored Square Buttons/Trading col_Square Button.png");
        loadTexture("Army col_Square", "data_Img/Menu/Square Buttons/Colored Square Buttons/Army col_Square Button.png");
        loadTexture("Worker col_Square", "data_Img/Menu/Square Buttons/Colored Square Buttons/Worker col_Square Button.png");


        loadTexture("Worker Square", "data_Img/Menu/Square Buttons/Square Buttons/Worker Square Button.png");
        loadTexture("Army Square", "data_Img/Menu/Square Buttons/Square Buttons/Army Square Button.png");
        loadTexture("Trading Square", "data_Img/Menu/Square Buttons/Square Buttons/Trading Square Button.png");
        loadTexture("Hammer Square", "data_Img/Menu/Square Buttons/Square Buttons/Hammer Square Button.png");
        loadTexture("City Square", "data_Img/Menu/Square Buttons/Square Buttons/City Square Button.png");
        loadTexture("Marker Square", "data_Img/Menu/Square Buttons/Square Buttons/Marker Square Button.png");

        // рабочие
        loadTexture("SlideB", "data_Img/GUI/Button01.png");

        // ресурсы
        loadTexture("SlideInventory", "data_Img/GUI/Inventory/MiniPanel01.jpg");
        loadTexture("horizontal_flip", "data_Img/GUI/Inventory/horizontal-flip.png");

        loadTexture("WOOD", "data_Img/GUI/Inventory/log.png");
        loadTexture("STONE", "data_Img/GUI/Inventory/stone-pile.png");
        loadTexture("IRON", "data_Img/GUI/Inventory/black-bar.png");
        loadTexture("LEATHER", "data_Img/GUI/Inventory/animal-hide.png");
        loadTexture("BEER", "data_Img/GUI/Inventory/beer-horn.png");
        loadTexture("WHEAT", "data_Img/GUI/Inventory/wheat.png");
        loadTexture("STEEL", "data_Img/GUI/Inventory/metal-bar.png");
        loadTexture("TOOLS", "data_Img/GUI/Inventory/screwdriver.png");
        loadTexture("SHIP", "data_Img/GUI/Inventory/drakkar.png");
        loadTexture("WEAPON", "data_Img/GUI/Inventory/flail.png");
        loadTexture("ARMOR", "data_Img/GUI/Inventory/gauntlet.png");
        loadTexture("GOLD", "data_Img/GUI/Inventory/gold-nuggets.png");
        loadTexture("COINS", "data_Img/GUI/Inventory/coins-pile.png");
        loadTexture("UNIT", "data_Img/GUI/Inventory/character.png");

        //меню
        for (int i = 1; i < 164; i++) {
            if (i < 10) {
                loadTexture("background_00" + i, "data_Img/Menu/828x512/00" + i + ".large.png");
            } else if (i < 100) {
                loadTexture("background_0" + i, "data_Img/Menu/828x512/0" + i + ".large.png");
            } else {
                loadTexture("background_" + i, "data_Img/Menu/828x512/" + i + ".large.png");
            }
        }


        loadTexture("New game col_Button", "data_Img/Menu/Large Buttons/Colored Large Buttons/New game col_Button.png");
        loadTexture("Settings col_Button", "data_Img/Menu/Large Buttons/Colored Large Buttons/Settings col_Button.png");
        loadTexture("Load col_Button", "data_Img/Menu/Large Buttons/Colored Large Buttons/Load col_Button.png");
        loadTexture("Quit col_Button", "data_Img/Menu/Large Buttons/Colored Large Buttons/Quit col_Button.png");
        loadTexture("Play col_Button", "data_Img/Menu/Large Buttons/Colored Large Buttons/Play col_Button.png");


        loadTexture("New game Button", "data_Img/Menu/Large Buttons/Large Buttons/New game Button.png");
        loadTexture("Settings Button", "data_Img/Menu/Large Buttons/Large Buttons/Settings Button.png");
        loadTexture("Load Button", "data_Img/Menu/Large Buttons/Large Buttons/Load Button.png");
        loadTexture("Quit Button", "data_Img/Menu/Large Buttons/Large Buttons/Quit Button.png");
        loadTexture("Play Button", "data_Img/Menu/Large Buttons/Large Buttons/Play Button.png");


        loadTexture("Slide", "data_Img/Menu/MiniPanel03.jpg");
        loadTexture("03", "data_Img/Menu/return_hover.png");
        loadTexture("03_d", "data_Img/Menu/return_idle.png");

        loadTexture("not_full", "data_Img/Menu/ProgressBar/BarV1_ProgressBarBorder.png");
        loadTexture("full", "data_Img/Menu/ProgressBar/BarV1_ProgressBar.png");

        // кнопочки для прогресс
        loadTexture("LCell01", "data_Img/Menu/ProgressBar/generated_LCell01.png");
        loadTexture("LCell02", "data_Img/Menu/ProgressBar/generated_LCell02.png");
        loadTexture("RCell01", "data_Img/Menu/ProgressBar/generated_RCell01.png");
        loadTexture("RCell02", "data_Img/Menu/ProgressBar/generated_RCell02.png");
        loadTexture("Cell01", "data_Img/Menu/generated_Cell01.png");
        loadTexture("Cell02", "data_Img/Menu/generated_Cell02.png");

        // штучка для текстовой штуки
        loadTexture("variation01", "data_Img/Menu/TextField/variation01.png");

        // гифки
        loadGif("load_gif", "data_Img/Menu/75.gif");


        // меню загрузки
        loadTexture("logo", "data_Img/Menu/logo_1.png");


        // глобальная карта
        // снега
        loadTexture("snow_base", "data_Img/GlobalMap/pole/land.png");
        loadTexture("snow_forest", "data_Img/GlobalMap/pole/forest.png");
        loadTexture("snow_mountain", "data_Img/GlobalMap/pole/mountain.png");

        // тайга
        loadTexture("midlands1_base", "data_Img/GlobalMap/midlands1/land.png");
        loadTexture("midlands1_forest", "data_Img/GlobalMap/midlands1/forest.png");
        loadTexture("midlands1_mountain", "data_Img/GlobalMap/midlands1/mountain.png");


        // средняя земля
        loadTexture("midlands2_base_1", "data_Img/GlobalMap/midlands2/land_1.png");
        loadTexture("midlands2_base_2", "data_Img/GlobalMap/midlands2/land_2.png");
        loadTexture("midlands2_base_3", "data_Img/GlobalMap/midlands2/land_3.png");
        loadTexture("midlands2_sand_1", "data_Img/GlobalMap/midlands2/sand_1.png");
        loadTexture("midlands2_mountain", "data_Img/GlobalMap/midlands2/mountain_1.png");
        loadTexture("midlands2_mountain1", "data_Img/GlobalMap/midlands2/mountain__1.png");
        loadTexture("midlands2_mountain2", "data_Img/GlobalMap/midlands2/mountain__2.png");


        loadTexture("midlands2_details_Trees_1", "data_Img/GlobalMap/midlands2/Details/Trees_1.png");
        loadTexture("midlands2_details_Trees_2", "data_Img/GlobalMap/midlands2/Details/Trees_2.png");
        loadTexture("midlands2_details_Trees_3", "data_Img/GlobalMap/midlands2/Details/Trees_3.png");
        loadTexture("midlands2_details_Trees_4", "data_Img/GlobalMap/midlands2/Details/Trees_4.png");
        loadTexture("midlands2_details_Trees_5", "data_Img/GlobalMap/midlands2/Details/Trees_5.png");

        // горячие земли
        loadTexture("desert_base", "data_Img/GlobalMap/desert/land.png");
        loadTexture("desert_forest", "data_Img/GlobalMap/desert/forest.png");
        loadTexture("desert_mountain", "data_Img/GlobalMap/desert/mountain.png");

        // вода
        loadTexture("depth_4", "data_Img/GlobalMap/water/depth_1_4.png");
        loadTexture("depth_3", "data_Img/GlobalMap/water/depth_1_3.png");
        loadTexture("depth_2", "data_Img/GlobalMap/water/depth_1_2.png");
        loadTexture("depth_1", "data_Img/GlobalMap/water/depth_1_1.png");

        // туман войны
        loadTexture("fogofwar_base", "data_Img/PaperMap/paper.png");
        loadTexture("fogofwar_base_water", "data_Img/PaperMap/water.png");
        loadTexture("paper1", "data_Img/PaperMap/paper1.png"); // условно город
        loadTexture("paper2", "data_Img/PaperMap/paper2.png"); // условно здание
        loadTexture("paper3", "data_Img/PaperMap/paper3.png"); // условно дорога

        loadTexture("fogofwar_details_mountain_2", "data_Img/PaperMap/Mountains2.png");
        loadTexture("fogofwar_details_Trees_2", "data_Img/PaperMap/Forest2.png");
        loadTexture("fogofwar_details_Trees_3", "data_Img/PaperMap/Forest3.png");

        // здания
        loadTexture("0", "data_Img/GlobalMap/build_1/0.png");
        loadTexture("MeltingFurnace", "data_Img/GlobalMap/build_1/MeltingFurnace.png"); //1
        loadTexture("BlastFurnace", "data_Img/GlobalMap/build_1/BlastFurnace.png");//2
        loadTexture("Mine", "data_Img/GlobalMap/build_1/Mine.png");//3
        loadTexture("TannerWorkshop", "data_Img/GlobalMap/build_1/TannerWorkshop.png");//4
        loadTexture("Brewery", "data_Img/GlobalMap/build_1/Brewery.png");//5
        loadTexture("MetallurgistsWorkshop", "data_Img/GlobalMap/build_1/MetallurgistsWorkshop.png");//6
        loadTexture("Sawmill", "data_Img/GlobalMap/build_1/Sawmill.png");//7
        loadTexture("WheatField", "data_Img/GlobalMap/build_1/WheatField.png");//8
        loadTexture("StonemasonWorkshop", "data_Img/GlobalMap/build_1/StonemasonWorkshop.png");//12
        loadTexture("Shipyard", "data_Img/GlobalMap/build_1/Shipyard.png");//13
        loadTexture("ResidentialAreas", "data_Img/GlobalMap/build_1/ResidentialAreas.png");//14
        loadTexture("Workshop", "data_Img/GlobalMap/build_1/Workshop.png");//15

        // дороги соединяют 6 сторон шестиугольника
        loadTexture("land_2456", "data_Img/GlobalMap/road/land_2456.png");
        loadTexture("land_2356", "data_Img/GlobalMap/road/land_2356.png");
        loadTexture("land_2346", "data_Img/GlobalMap/road/land_2346.png");
        loadTexture("land_1356", "data_Img/GlobalMap/road/land_1356.png");
        loadTexture("land_1346", "data_Img/GlobalMap/road/land_1346.png");
        loadTexture("land_1345", "data_Img/GlobalMap/road/land_1345.png");
        loadTexture("land_1246", "data_Img/GlobalMap/road/land_1246.png");
        loadTexture("land_1245", "data_Img/GlobalMap/road/land_1245.png");
        loadTexture("land_1236", "data_Img/GlobalMap/road/land_1236.png");
        loadTexture("land_1235", "data_Img/GlobalMap/road/land_1235.png");
        loadTexture("land_1234", "data_Img/GlobalMap/road/land_1234.png");
        loadTexture("land_456", "data_Img/GlobalMap/road/land_456.png");
        loadTexture("land_356", "data_Img/GlobalMap/road/land_356.png");
        loadTexture("land_346", "data_Img/GlobalMap/road/land_346.png");
        loadTexture("land_345", "data_Img/GlobalMap/road/land_345.png");
        loadTexture("land_256", "data_Img/GlobalMap/road/land_256.png");
        loadTexture("land_246", "data_Img/GlobalMap/road/land_246.png");
        loadTexture("land_245", "data_Img/GlobalMap/road/land_245.png");
        loadTexture("land_236", "data_Img/GlobalMap/road/land_236.png");
        loadTexture("land_235", "data_Img/GlobalMap/road/land_235.png");
        loadTexture("land_234", "data_Img/GlobalMap/road/land_234.png");
        loadTexture("land_156", "data_Img/GlobalMap/road/land_156.png");
        loadTexture("land_146", "data_Img/GlobalMap/road/land_146.png");
        loadTexture("land_145", "data_Img/GlobalMap/road/land_145.png");
        loadTexture("land_136", "data_Img/GlobalMap/road/land_136.png");
        loadTexture("land_135", "data_Img/GlobalMap/road/land_135.png");
        loadTexture("land_134", "data_Img/GlobalMap/road/land_134.png");
        loadTexture("land_126", "data_Img/GlobalMap/road/land_126.png");
        loadTexture("land_125", "data_Img/GlobalMap/road/land_125.png");
        loadTexture("land_124", "data_Img/GlobalMap/road/land_124.png");
        loadTexture("land_123", "data_Img/GlobalMap/road/land_123.png");
        loadTexture("land_56", "data_Img/GlobalMap/road/land_56.png");
        loadTexture("land_46", "data_Img/GlobalMap/road/land_46.png");
        loadTexture("land_45", "data_Img/GlobalMap/road/land_45.png");
        loadTexture("land_36", "data_Img/GlobalMap/road/land_36.png");
        loadTexture("land_35", "data_Img/GlobalMap/road/land_35.png");
        loadTexture("land_34", "data_Img/GlobalMap/road/land_34.png");
        loadTexture("land_26", "data_Img/GlobalMap/road/land_26.png");
        loadTexture("land_25", "data_Img/GlobalMap/road/land_25.png");
        loadTexture("land_24", "data_Img/GlobalMap/road/land_24.png");
        loadTexture("land_23", "data_Img/GlobalMap/road/land_23.png");
        loadTexture("land_16", "data_Img/GlobalMap/road/land_16.png");
        loadTexture("land_15", "data_Img/GlobalMap/road/land_15.png");
        loadTexture("land_14", "data_Img/GlobalMap/road/land_14.png");
        loadTexture("land_13", "data_Img/GlobalMap/road/land_13.png");
        loadTexture("land_12", "data_Img/GlobalMap/road/land_12.png");
    }

    public Texture[] textureSelection(int biome, int type, String road, BuildingsType building, int fog) {
        if (building != null) {
            if (fog == 0){switch (building) {
                case MeltingFurnace:
                    return new Texture[]{getTexture("midlands2_base_1"), getTexture("MeltingFurnace")};
                case BlastFurnace:
                    return new Texture[]{getTexture("midlands2_base_1"), getTexture("BlastFurnace")};
                case Mine:
                    return new Texture[]{getTexture("midlands2_base_1"), getTexture("Mine")};
                case TannerWorkshop:
                    return new Texture[]{getTexture("midlands2_base_1"), getTexture("TannerWorkshop")};
                case MetallurgistsWorkshop:
                    return new Texture[]{getTexture("midlands2_base_1"), getTexture("MetallurgistsWorkshop")};
                case Sawmill:
                    return new Texture[]{getTexture("midlands2_base_1"), getTexture("Sawmill")};
                case StonemasonWorkshop:
                    return new Texture[]{getTexture("midlands2_base_1"), getTexture("StonemasonWorkshop")};
                case Shipyard:
                    return new Texture[]{getTexture("midlands2_base_1"), getTexture("Shipyard")};
                case ResidentialAreas:
                    return new Texture[]{getTexture("midlands2_base_1"), getTexture("ResidentialAreas")};
                case Workshop:
                    return new Texture[]{getTexture("midlands2_base_1"), getTexture("Workshop")};
                case CITY_CENTER:
                    return new Texture[]{getTexture("midlands2_base_1"), getTexture("0")};

            }}
            if (fog == 1){switch (building) {
                case MeltingFurnace:
                    return new Texture[]{getTexture("paper2"), getTexture("MeltingFurnace")};
                case BlastFurnace:
                    return new Texture[]{getTexture("paper2"), getTexture("BlastFurnace")};
                case Mine:
                    return new Texture[]{getTexture("paper2"), getTexture("Mine")};
                case TannerWorkshop:
                    return new Texture[]{getTexture("paper2"), getTexture("TannerWorkshop")};
                case MetallurgistsWorkshop:
                    return new Texture[]{getTexture("paper2"), getTexture("MetallurgistsWorkshop")};
                case Sawmill:
                    return new Texture[]{getTexture("paper2"), getTexture("Sawmill")};
                case StonemasonWorkshop:
                    return new Texture[]{getTexture("paper2"), getTexture("StonemasonWorkshop")};
                case Shipyard:
                    return new Texture[]{getTexture("paper2"), getTexture("Shipyard")};
                case ResidentialAreas:
                    return new Texture[]{getTexture("paper2"), getTexture("ResidentialAreas")};
                case Workshop:
                    return new Texture[]{getTexture("paper2"), getTexture("Workshop")};
                case CITY_CENTER:
                    return new Texture[]{getTexture("paper2"), getTexture("0")};

            }}
            if (fog == 2){switch (building) {
                case MeltingFurnace:
                    return new Texture[]{getTexture("paper3"), getTexture("MeltingFurnace")};
                case BlastFurnace:
                    return new Texture[]{getTexture("paper3"), getTexture("BlastFurnace")};
                case Mine:
                    return new Texture[]{getTexture("paper3"), getTexture("Mine")};
                case TannerWorkshop:
                    return new Texture[]{getTexture("paper3"), getTexture("TannerWorkshop")};
                case MetallurgistsWorkshop:
                    return new Texture[]{getTexture("paper3"), getTexture("MetallurgistsWorkshop")};
                case Sawmill:
                    return new Texture[]{getTexture("paper3"), getTexture("Sawmill")};
                case StonemasonWorkshop:
                    return new Texture[]{getTexture("paper3"), getTexture("StonemasonWorkshop")};
                case Shipyard:
                    return new Texture[]{getTexture("paper3"), getTexture("Shipyard")};
                case ResidentialAreas:
                    return new Texture[]{getTexture("paper3"), getTexture("ResidentialAreas")};
                case Workshop:
                    return new Texture[]{getTexture("paper3"), getTexture("Workshop")};
                case CITY_CENTER:
                    return new Texture[]{getTexture("paper3"), getTexture("0")};

            }}
        }
        if (!road.equals("")) {
            Texture t = getTexture("land_" + road);
            if (t == null) {
                return new Texture[]{getTexture("paper3"), getTexture("")};
            } else {
                return new Texture[]{getTexture("land_" + road), getTexture("")};
            }
        }
        switch (biome) {
            case 0:
                // вода
                switch (type) {
                    case 0:
                        return new Texture[]{getTexture("depth_4"), getTexture("")};
                    case 1:
                        return new Texture[]{getTexture("depth_3"), getTexture("")};
                    case 2:
                        return new Texture[]{getTexture("depth_2"), getTexture("")};
                    case 3:
                        return new Texture[]{getTexture("depth_1"), getTexture("")};
                }
            case 1:
            case 2:
            case 3:
                switch (type) {
                    case 0:
                        return new Texture[]{getTexture("midlands2_base_" + (random.nextInt(3) + 1)), getTexture("")};
                    case 1:
                        return new Texture[]{getTexture("midlands2_base_" + (random.nextInt(3) + 1)), getTexture("midlands2_details_Trees_" + (random.nextInt(5) + 1))};
                    case 2:
                        return new Texture[]{getTexture("midlands2_mountain"), getTexture("midlands2_mountain" + (random.nextInt(2) + 1))};
                    case 3:
                        return new Texture[]{getTexture("midlands2_sand_1"), getTexture("")};
                }
            case 4:
                switch (type) {
                    case 0:
                        return new Texture[]{getTexture("midlands2_base_" + (random.nextInt(3) + 1)), getTexture("")};
                    case 1:
                        return new Texture[]{getTexture("midlands2_base_" + (random.nextInt(3) + 1)), getTexture("midlands2_details_Trees_" + (random.nextInt(5) + 1))};
                    case 2:
                        return new Texture[]{getTexture("midlands2_mountain"), getTexture("midlands2_mountain" + (random.nextInt(2) + 1))};
                    case 3:
                        return new Texture[]{getTexture("midlands2_sand_1"), getTexture("")};
                }
        }

        return new Texture[]{getTexture(""), getTexture("")};
    }

    public Texture selectionTexturesForMenu(String type) {
        switch (type) {
            case "background":
                int a = random.nextInt(163);
                if (a < 10) {
                    return getTexture("background_00" + a);
                } else if (a < 100) {
                    return getTexture("background_0" + a);
                } else {
                    return getTexture("background_" + a);
                }
        }
        return getTexture(type);
    }

    public Texture gettingAResource(ResourceType type) {
        switch (type) {
            case BEER:
                return getTexture("BEER");
            case GOLD:
                return getTexture("GOLD");
            case IRON:
                return getTexture("IRON");
            case SHIP:
                return getTexture("SHIP");
            case WOOD:
                return getTexture("WOOD");
            case ARMOR:
                return getTexture("ARMOR");
            case COINS:
                return getTexture("COINS");
            case STEEL:
                return getTexture("STEEL");
            case STONE:
                return getTexture("STONE");
            case TOOLS:
                return getTexture("TOOLS");
            case WHEAT:
                return getTexture("WHEAT");
            case WEAPON:
                return getTexture("WEAPON");
            case LEATHER:
                return getTexture("LEATHER");
            case UNIT:
                return getTexture("UNIT");
        }
        return getTexture("");
    }
}

