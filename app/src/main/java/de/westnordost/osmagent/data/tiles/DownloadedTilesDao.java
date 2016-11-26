package de.westnordost.osmagent.data.tiles;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import de.westnordost.osmagent.Prefs;

/** Keeps info in which areas quests have been downloaded already in a tile grid of zoom level 14
 *  (~0.022° per tile -> a few kilometers sidelength)*/
public class DownloadedTilesDao
{
	private final SQLiteOpenHelper dbHelper;

	private final SQLiteStatement insert;

	@Inject
	public DownloadedTilesDao(SQLiteOpenHelper dbHelper)
	{
		this.dbHelper = dbHelper;

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		insert = db.compileStatement(
				"INSERT OR REPLACE INTO " +
				DownloadedTilesTable.NAME + " ("+
					DownloadedTilesTable.Columns.X+","+
					DownloadedTilesTable.Columns.Y+","+
					DownloadedTilesTable.Columns.QUEST_TYPE+","+
					DownloadedTilesTable.Columns.DATE+
				") values (?,?,?,?);");
	}

	/** Persist that the given quest type has been downloaded in every tile in the given tile range */
	public void putQuestType(Rect tiles, String questTypeName)
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.beginTransaction();
		long time = System.currentTimeMillis();
		for(int x = tiles.left; x <= tiles.right; ++x)
		{
			for(int y = tiles.top; y <= tiles.bottom; ++y)
			{
				insert.bindLong(1,x);
				insert.bindLong(2,y);
				insert.bindString(3, questTypeName);
				insert.bindLong(4,time);
				insert.executeInsert();
				insert.clearBindings();
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/** @return a list of quest type names which have already been downloaded in every tile in the
	 *          given tile range */
	public List<String> getQuestTypeNames(Rect tiles, long ignoreOlderThan)
	{
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		int tileCount = (1 + tiles.width()) * (1 + tiles.height());

		String where =
				DownloadedTilesTable.Columns.X + " BETWEEN ? AND ? AND " +
				DownloadedTilesTable.Columns.Y + " BETWEEN ? AND ? AND " +
				DownloadedTilesTable.Columns.DATE + " > ?";
		String[] whereArgs = {
				String.valueOf(tiles.left), String.valueOf(tiles.right),
				String.valueOf(tiles.top), String.valueOf(tiles.bottom),
				String.valueOf(ignoreOlderThan)
		};

		String[] cols = { DownloadedTilesTable.Columns.QUEST_TYPE};
		String groupBy = DownloadedTilesTable.Columns.QUEST_TYPE;
		String having = "COUNT(*) >= " + tileCount;

		Cursor cursor = db.query(DownloadedTilesTable.NAME,	cols, where, whereArgs, groupBy, having, null);

		if(cursor.getCount() == 0) return Collections.emptyList();

		List<String> result = new ArrayList<>(cursor.getCount());

		try
		{
			if(cursor.moveToFirst())
			{
				while(!cursor.isAfterLast())
				{
					String questTypeName = cursor.getString(0);
					result.add(questTypeName);
					cursor.moveToNext();
				}
			}
		}
		finally
		{
			cursor.close();
		}

		return result;
	}
}
