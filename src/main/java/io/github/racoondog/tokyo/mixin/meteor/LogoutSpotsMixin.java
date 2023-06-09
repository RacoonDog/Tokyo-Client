package io.github.racoondog.tokyo.mixin.meteor;

import io.github.racoondog.tokyo.utils.misc.CSVWriter;
import io.github.racoondog.tokyo.utils.misc.ExportUtils;
import io.github.racoondog.tokyo.utils.misc.FileUtils;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.LogoutSpots;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(value = LogoutSpots.class, remap = false)
public abstract class LogoutSpotsMixin extends Module {
    @Shadow @Final private List<?> players;

    private LogoutSpotsMixin(Category category, String name, String description) {
        super(category, name, description);
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();
        WButton export = list.add(theme.button("Export as .CSV")).expandX().widget();
        export.action = () -> {
            Path exportLocation = ExportUtils.computePath(ExportUtils.WORLDNAME.get() + "/" + "logout_spots_" + ExportUtils.DATETIME.get(), ".csv");
            FileUtils.ensureDirectoryExists(exportLocation);
            try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(exportLocation))) {
                writer.writeNext("X", "Y", "Z", "Uuid", "Name");
                for (var entry : players) {
                    int x = Math.round((float) ((ILogoutSpotsEntry) entry).tokyo$getX());
                    int y = Math.round((float) ((ILogoutSpotsEntry) entry).tokyo$getY());
                    int z = Math.round((float) ((ILogoutSpotsEntry) entry).tokyo$getZ());
                    writer.writeNext(x, y, z, ((ILogoutSpotsEntry) entry).tokyo$getUuid(), ((ILogoutSpotsEntry) entry).tokyo$getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        return table;
    }
}
