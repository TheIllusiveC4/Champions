/*
 * Copyright (C) 2018-2019  C4
 *
 * This file is part of Champions, a mod made for Minecraft.
 *
 * Champions is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Champions is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.champions.common.util;

import c4.champions.Champions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
* Derivative of info.tehnut.soulshardrespawn.core.util.JsonUtil from Soul Shards Respawn by TehNut
* Soul Shards Respawn is distributed under the MIT License
*/
public class JsonUtil {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static <T> T[] fromJson(@Nonnull TypeToken<T[]> token, @Nonnull File file, @Nonnull T[] defaults) {

        if (!file.exists()) {
            toJson(token, file, defaults);
            return defaults;
        } else {

            try (FileReader reader = new FileReader(file)) {
                return GSON.fromJson(reader, token.getType());
            } catch (IOException e) {
                Champions.logger.log(Level.ERROR, "Error reading Json file");
                return defaults;
            }
        }
    }

    private static <T> void toJson(@Nonnull TypeToken<T[]> token, @Nonnull File file, @Nonnull T[] defaults) {

        if (!file.exists()) {
            try {
                FileUtils.forceMkdirParent(file);
                file.createNewFile();
            } catch (IOException e) {
                Champions.logger.log(Level.ERROR, "Error creating Json file");
                return;
            }
        }

        try (FileWriter writer = new FileWriter(file)){
            writer.write(getJson(defaults, token));
        } catch (IOException e) {
            Champions.logger.log(Level.ERROR, "Error creating Json file");
        }
    }

    private static <T> String getJson(@Nonnull T[] elements, @Nonnull TypeToken<T[]> token) {
        return GSON.toJson(elements, token.getType());
    }
}
