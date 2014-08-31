package jas.compatability.tf;

import jas.api.StructureInterpreter;
import jas.common.ReflectionHelper;
import jas.common.spawner.biome.group.BiomeHelper;
import jas.common.spawner.biome.structure.StructureInterpreterHelper;
import jas.common.spawner.creature.handler.parsing.ParsingHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.MapGenTFMajorFeature;
import twilightforest.world.TFWorld;

public class StructureInterpreterTwilightForest implements StructureInterpreter {
	public final String UNDERGROUND_STUCTURE_KEY_PREFIX = "UG_SPAWNLIST_";
    private HashMap<String, Integer> featureNameToID = new HashMap<String, Integer>();
    private HashMap<String, Integer> undergroundSpawnList = new HashMap<String, Integer>();

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Collection<String> getStructureKeys() {
        Collection<String> collection = new ArrayList();
        for (int i = 0; i < TFFeature.featureList.length; i++) {
            if (TFFeature.featureList[i] == null) {
                continue;
            }
            List spawnableMonsterList = ReflectionHelper.getFieldFromReflection("spawnableMonsterLists",
                    TFFeature.featureList[i], List.class);
            if (spawnableMonsterList != null) {
                for (int j = 0; j < spawnableMonsterList.size(); j++) {
                    featureNameToID.put(TFFeature.featureList[i].name, i);
                    collection.add(TFFeature.featureList[i].name + "_" + j);
                }
            }
        }
		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
			if (biome != null && biome instanceof TFBiomeBase) {
				collection.add(UNDERGROUND_STUCTURE_KEY_PREFIX + BiomeHelper.getPackageName(biome));
			}
		}
        return collection;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Collection<SpawnListEntry> getStructureSpawnList(String structureKey) {
        Collection<SpawnListEntry> collection = new ArrayList();
        if(structureKey.startsWith(UNDERGROUND_STUCTURE_KEY_PREFIX)) {
        	String underGroundBiome = structureKey.substring(UNDERGROUND_STUCTURE_KEY_PREFIX.length());
			for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
				if(biome != null && biome instanceof TFBiomeBase && underGroundBiome.equalsIgnoreCase(BiomeHelper.getPackageName(biome))) {
					collection.addAll(((TFBiomeBase) biome).getUndergroundSpawnableList());
					break;
				}
			}
        } else {
            String[] keyParts = structureKey.split("_");
            String featureName = keyParts[0];
            int index = ParsingHelper.parseFilteredInteger(keyParts[1], -1, structureKey);
            TFFeature feature = TFFeature.featureList[featureNameToID.get(featureName)];
            for (EnumCreatureType creatureType : EnumCreatureType.values()) {
                collection.addAll(feature.getSpawnableList(creatureType, index));
            }
        }
        return collection;
    }

    @Override
    public String areCoordsStructure(World world, int xCoord, int yCoord, int zCoord) {
        ChunkProviderTwilightForest chunkProviderTwilightForest = StructureInterpreterHelper.getInnerChunkProvider(
                world, ChunkProviderTwilightForest.class);
        if (chunkProviderTwilightForest != null) {
            TFFeature nearestFeature = TFFeature.getNearestFeature(xCoord >> 4, zCoord >> 4, world);

            if (nearestFeature != TFFeature.nothing) {
            	if (chunkProviderTwilightForest.isStructureConquered(xCoord, yCoord, zCoord)) {
    				return null;
    			}
                MapGenTFMajorFeature mapGenTFMajorFeature;
                mapGenTFMajorFeature = ReflectionHelper.getFieldFromReflection("majorFeatureGenerator",
                        chunkProviderTwilightForest, MapGenTFMajorFeature.class);

                int spawnListIndex = mapGenTFMajorFeature.getSpawnListIndexAt(xCoord, yCoord, zCoord);
                if (spawnListIndex >= 0) {
                    return nearestFeature.name + "_" + Integer.toString(spawnListIndex);
                }
            }

			BiomeGenBase biome = world.getBiomeGenForCoords(xCoord, zCoord);
			if ((yCoord < TFWorld.SEALEVEL && (biome instanceof TFBiomeBase))) {
				return UNDERGROUND_STUCTURE_KEY_PREFIX + BiomeHelper.getPackageName(biome);
			}
		}
        return null;
    }

    @Override
    public boolean shouldUseHandler(World world, BiomeGenBase biomeGenBase) {
        return world.provider.dimensionId == TwilightForestMod.dimensionID;
    }
}
