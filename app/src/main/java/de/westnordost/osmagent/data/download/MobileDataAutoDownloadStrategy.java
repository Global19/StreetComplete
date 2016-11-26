package de.westnordost.osmagent.data.download;

import android.content.SharedPreferences;

import javax.inject.Inject;

import de.westnordost.osmagent.data.QuestTypes;
import de.westnordost.osmagent.data.osm.persist.OsmQuestDao;
import de.westnordost.osmagent.data.tiles.DownloadedTilesDao;

public class MobileDataAutoDownloadStrategy extends AActiveRadiusStrategy
{
	@Inject public MobileDataAutoDownloadStrategy(OsmQuestDao osmQuestDB,
										  DownloadedTilesDao downloadedTilesDao,
										  QuestTypes questTypes, SharedPreferences prefs)
	{
		super(osmQuestDB, downloadedTilesDao, questTypes, prefs);
	}

	@Override public int getQuestTypeDownloadCount()
	{
		return 3;
	}

	@Override protected int getMinQuestsInActiveRadiusPerKm2()
	{
		return 8;
	}

	@Override protected int getActiveRadius()
	{
		return 400;
	}

	@Override protected int getDownloadRadius()
	{
		return 800;
	}
}
