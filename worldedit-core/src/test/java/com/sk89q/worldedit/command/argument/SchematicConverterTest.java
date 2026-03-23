/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.sk89q.worldedit.command.argument;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.internal.command.CommandUtil;
import com.sk89q.worldedit.internal.schematic.SchematicsManager;
import com.sk89q.worldedit.internal.util.Substring;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SchematicConverterTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "iron farm-converted.schem",
        "starter base.schem",
        "farms/iron farm 2.schem"
    })
    void getSuggestionsQuotesAnySchematicNameContainingSpaces(String schematicName) throws Exception {
        SchematicConverter converter = newConverterWithSchematics(Set.of(schematicName));
        String normalizedName = Path.of(schematicName).toString();

        assertEquals(List.of(quote(normalizedName)), converter.getSuggestions(typedPrefix(normalizedName), mock(InjectedValueAccess.class)));
        assertEquals(List.of(quote(normalizedName)), converter.getSuggestions("\"" + typedPrefix(normalizedName), mock(InjectedValueAccess.class)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "iron-farm-converted.schem",
        "farms/iron-farm-2.schem"
    })
    void getSuggestionsLeavesSchematicNamesWithoutSpacesUnquoted(String schematicName) throws Exception {
        SchematicConverter converter = newConverterWithSchematics(Set.of(schematicName));
        String normalizedName = Path.of(schematicName).toString();

        assertEquals(
            List.of(normalizedName),
            converter.getSuggestions(typedPrefix(normalizedName), mock(InjectedValueAccess.class))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "iron farm-converted.schem",
        "starter base.schem",
        "farms/iron farm 2.schem"
    })
    void fixSuggestionsHandlesQuotedSuggestionsContainingSpaces(String schematicName) {
        String normalizedName = Path.of(schematicName).toString();

        assertEquals(
            List.of(quote(normalizedName)),
            CommandUtil.fixSuggestions(
                "//schem load ",
                List.of(Substring.wrap(quote(normalizedName), 13, 13))
            )
        );
    }

    private static SchematicConverter newConverterWithSchematics(Set<String> schematicNames) throws Exception {
        Path schematicsRoot = Path.of("schematics");
        WorldEdit worldEdit = mock(WorldEdit.class);
        SchematicsManager schematicsManager = mock(SchematicsManager.class);
        when(worldEdit.getSchematicsManager()).thenReturn(schematicsManager);
        when(schematicsManager.getRoot()).thenReturn(schematicsRoot);
        when(schematicsManager.getSchematicPaths()).thenReturn(schematicNames.stream()
            .map(schematicsRoot::resolve)
            .collect(java.util.stream.Collectors.toSet()));

        return newConverter(worldEdit);
    }

    private static String typedPrefix(String input) {
        return input.substring(0, Math.min(4, input.length()));
    }

    private static String quote(String input) {
        return '"' + input + '"';
    }

    private static SchematicConverter newConverter(WorldEdit worldEdit) throws Exception {
        Constructor<SchematicConverter> constructor = SchematicConverter.class.getDeclaredConstructor(WorldEdit.class);
        constructor.setAccessible(true);
        return constructor.newInstance(worldEdit);
    }
}
