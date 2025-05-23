package cn.xiaowine.lcmanager.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import java.io.File

class DesktopDatabaseFactory : DatabaseFactory {
    override fun createDatabase(): RoomDatabase.Builder<UserDatabase> {
//        val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
        val dbFile = File(System.getProperty("./"), "my_room.db")
        return Room.databaseBuilder<UserDatabase>(
            name = dbFile.absolutePath,
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
    }
}
