# GreenDAO 学习笔记

[GreenDAO Github地址][1]
[GreenDAO 官方网站][2]

## 介绍
GreenDAO是一个开源的Android端ORM(Object Relational Mapping 对象关系映射)框架，可以让用户使用Java方法来做增删改查等动作。

![GreenDAO结构][3]

## 使用

### 1. 添加gradle依赖

在**project**的build.gradle文件中添加：

注意，以下的greendao请输入**最新版本**。[在这里可以查看最新版本][4]
```
// In your root build.gradle file:
buildscript {
    repositories {
        jcenter()
        mavenCentral() // add repository
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.0'
        classpath 'org.greenrobot:greendao-gradle-plugin:3.2.2' // add plugin
    }
}
```

在具体的**module**的build.gradle文件中添加：

```
// In your app projects build.gradle file:
apply plugin: 'com.android.application'
apply plugin: 'org.greenrobot.greendao' // apply plugin
 
dependencies {
    compile 'org.greenrobot:greendao:3.2.2' // add library
}
```

### 2. 设置数据库版本等信息
在module的build.gradle文件添加：

```
greendao {
    schemaVersion 1
}
```
schemaVersion的值表示数据库版本号。

另外，用户还可以设置daoPackage属性。

- daoPackage表示通过gradle插件生成的数据库相关文件的包名，默认为你的entity所在的包名。

### 3. 编写实体类

```
@Entity
public class Player {
    @Id
    private Long id;
    @Unique
    private String name;
    private Integer age;
}
```

点击build后，AndroidStudio会自动生成这个类的其他部分：

```
@Entity
public class Player {
    @Id
    private Long id;
    @Unique
    private String name;
    private Integer age;
    @Generated(hash = 1461101279)
    public Player(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    @Generated(hash = 30709322)
    public Player() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return this.age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
}
```

同时，AS会默认在`GreenDAOLearn\app\build\generated\source\greendao\com\ysx\greendaolearn\entity`目录下生成三个文件：

- DaoMaster.java
- DaoSession.java
- PlayerDao.java

![greendao_gen][5]

由于我们在.gitignore中添加了build，则该目录下的文件不会放在git库中，这些代码每次build的时候都会重新生成。

#### 实体类中常用的注解：

```
@Entity 　表明这个实体类会在数据库中生成一个与之相对应的表。 
@Id 　对应数据表中的 Id 字段，有了解数据库的话，是一条数据的唯一标识。 
@Property(nameInDb = “STUDENTNUM”) 　表名这个属性对应数据表中的 STUDENTNUM 字段。 
@Property 　可以自定义字段名，注意外键不能使用该属性 
@NotNull 　该属性值不能为空 
@Transient 　该属性不会被存入数据库中 
@Unique 　表名该属性在数据库中只能有唯一值
```

### 4. 操作数据库

#### 初始化数据库：

```
    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "players.db";
    private DaoSession mDaoSession;
    /**
     * 初始化DaoSession
     * 即获取一个全局的DaoSession实例
     * // TODO: 2017/10/31 可以使用一个单例类单独管理这个对象
     */
    private void initDaoSession() {
        DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(
                mContext.getApplicationContext(), DATABASE_NAME, null);
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();
    }
```

全局保存一个DaoSession对象，用来对数据库进行操作。

#### 增

```
    /**
     * 插入一条数据
     *
     * @param player
     */
    private void insertData(Player player) {
        PlayerDao playerDao = mDaoSession.getPlayerDao();
        playerDao.insert(player);
    }
```

#### 删

```
    /**
     * 根据id删除一条数据
     *
     * @param id
     */
    private void deleteData(long id) {
        PlayerDao playerDao = mDaoSession.getPlayerDao();
        playerDao.deleteByKey(id);
    }
```

#### 改

```
    /**
     * 更新一条数据
     * 更新年龄
     *
     * @param id
     * @param age
     */
    private void updateData(long id, int age) {
        Log.d(TAG, "updateData: id: " + id + ", age: " + age);
        PlayerDao playerDao = mDaoSession.getPlayerDao();
        Player player = playerDao.queryBuilder()
                .where(PlayerDao.Properties.Id.eq(id))
                .build()
                .unique();
        player.setAge(age);
        playerDao.update(player);
    }
```

#### 查

```
    /**
     * 获取全部数据，按照Id升序排列
     *
     * @return 数据列表
     */
    private List<Player> getAllData() {
        PlayerDao playerDao = mDaoSession.getPlayerDao();
        return playerDao.queryBuilder()
                .orderAsc(PlayerDao.Properties.Id)
                .build()
                .list();
    }
```

## 混淆配置

```
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
```

## 使用一个单例来管理DaoSession

编写一个单例类，可以全局保留唯一一个DaoSession对象。各Activity都可以调用这个对象，避免了创建多个对象的开销。在Application的onCreate()方法中进行初始化。

```
public class GreenDaoManager {
    private static final String TAG = "GreenDaoManager";

    private static final String DATABASE_NAME = "players.db";
    /**
     * 全局保持一个DaoSession
     */
    private DaoSession daoSession;

    private boolean isInited;

    private static final class GreenDaoManagerHolder {
        private static final GreenDaoManager sInstance = new GreenDaoManager();
    }

    public static GreenDaoManager getInstance() {
        return GreenDaoManagerHolder.sInstance;
    }

    private GreenDaoManager() {

    }

    /**
     * 初始化DaoSession
     *
     * @param context
     */
    public void init(Context context) {
        if (!isInited) {
            DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(
                    context.getApplicationContext(), DATABASE_NAME, null);
            DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
            daoSession = daoMaster.newSession();
            isInited = true;
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
```

初始化：

```
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        GreenDaoManager.getInstance().init(getApplicationContext());
    }
}
```

在AndroidManifest.xml中该Application的名字：

```
    <application
        android:name=".MyApplication"
        // 其他代码...
    </application>
```

在Activity中使用单例类：

```
    /**
     * 获取一个全局的DaoSession实例
     */
    private void initDaoSession() {
        mDaoSession = GreenDaoManager.getInstance().getDaoSession();
    }
```

获取到DaoSession对象之后，可以对其进行增删改查等操作。

## 数据库升级

我们可以自定义一个MyOpenHelper类，来继承DaoMaster.OpenHelper，当初始化数据库的时候，我们就可以调用这个类来新建对象:

```
    MyOpenHelper openHelper = new MyOpenHelper(
                    context.getApplicationContext(), DATABASE_NAME, null);
```

```
    /**
     * 定义一个MySQLiteOpenHelper类，用来处理数据库升级
     */
    static class MyOpenHelper extends DaoMaster.OpenHelper {

        public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            super.onUpgrade(db, oldVersion, newVersion);
        }
    }
```

在app的build.gradle文件中，将greendao 里边的schemaVersion字段增加，则做到了数据库升级的效果。再次运行程序，就会调用我们的MyOpenHelper的onUpgradle方法。

在这里，我们采用github上很有用的数据库升级数据迁移开元库[GreenDaoUpgradeHelper](https://github.com/yuweiguocn/GreenDaoUpgradeHelper)。在数据库升级的时候，做数据迁移。具体使用方法为：

假如为Player添加一个Integer的champion字段，用来表示获得的总冠军次数。

1. 将schemaVersion的值由1改为2.
2. 数据迁移：覆盖MyOpenHelper的onUpgrade方法。即调用MigrationHelper的migrate方法，实现接口ReCreateAllTableListener的两个方法（这是固定的），再将PlayerDao.class作为另个一个参数传入。
3. 修改显示champion的Adapter的方法：

```
    if (player.getChampion() == null) {
            holder.mTvChampion.setVisibility(View.GONE);
        } else {
            holder.mTvChampion.setVisibility(View.VISIBLE);
            holder.mTvChampion.setText(mContext.getString(R.string.player_champion, player.getChampion()));
        }
```

```
    /**
     * 定义一个MySQLiteOpenHelper类，用来处理数据库升级
     */
    static class MyOpenHelper extends DaoMaster.OpenHelper {

        public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            super.onUpgrade(db, oldVersion, newVersion);
            Log.d(TAG, "onUpgrade: old: " + oldVersion + ", new: " + newVersion);
            if (oldVersion <= 1) {
                MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        DaoMaster.createAllTables(db, ifNotExists);
                    }

                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, ifExists);
                    }
                }, PlayerDao.class);

            }

        }
    }
```

### **注意**

新添加的字段的数据类型必须是对象类型，如果是基本数据类型，则需要改为其对应的包装类，否则，在数据库升级做数据迁移的时候，就会出现异常`android.database.sqlite.SQLiteConstraintException`，导致数据迁移失败，对于用户的使用体验是很差的。

![数据迁移失败][7]


# [Demo源代码地址][6]

[GreenDaoUpgradeHelper地址][8]

# 参考

http://blog.csdn.net/zone_/article/details/69054997

http://blog.csdn.net/njweiyukun/article/details/51893092


  [1]: https://github.com/greenrobot/greenDAO
  [2]: http://greenrobot.org/greendao/
  [3]: http://greenrobot.org/wordpress/wp-content/uploads/greenDAO-orm-320.png
  [4]: http://search.maven.org/#search%7Cga%7C1%7Cg:%22org.greenrobot%22%20AND%20a:%22greendao%22
  [5]: http://img.blog.csdn.net/20171031192043622?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvTmV4dF9TZWNvbmQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast
  [6]: https://github.com/YoungBear/GreenDAOLean
  [7]: http://img.blog.csdn.net/20171103000530238?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvTmV4dF9TZWNvbmQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast
  [8]: https://github.com/yuweiguocn/GreenDaoUpgradeHelper