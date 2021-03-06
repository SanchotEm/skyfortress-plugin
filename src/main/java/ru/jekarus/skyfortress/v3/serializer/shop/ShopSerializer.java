package ru.jekarus.skyfortress.v3.serializer.shop;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import ru.jekarus.skyfortress.v3.SkyFortressPlugin;
import ru.jekarus.skyfortress.v3.gui.ShopGui;
import ru.jekarus.skyfortress.v3.serializer.SfSerializers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ShopSerializer {

    private final SkyFortressPlugin plugin;
    private final Path directory;
    private final Path shop;

    public ShopSerializer(SkyFortressPlugin plugin)
    {
        this.plugin = plugin;
        this.directory = Sponge.getConfigManager().getPluginConfig(this.plugin).getDirectory();
        this.shop = Paths.get(this.directory.toString(), "/shop.conf");
    }

    public void load()
    {
        if (!Files.exists(this.shop))
        {
            this.createDefault();
        }
        if (!Files.exists(this.shop))
        {
            return;
        }
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setPath(this.shop)
                .setDefaultOptions(ConfigurationOptions.defaults().setSerializers(SfSerializers.SERIALIZERS))
                .build();
        try
        {
            CommentedConfigurationNode node = loader.load();
            this.construct(node);
        }
        catch (IOException | ObjectMappingException e)
        {
            e.printStackTrace();
        }
    }

    private void construct(CommentedConfigurationNode node) throws ObjectMappingException
    {
        CommentedConfigurationNode gui = node.getNode("shop");
        ShopGui.INSTANCE = gui.getValue(TypeToken.of(ShopGui.class));
    }

    private void createDefault()
    {

        if (!Files.exists(this.directory))
        {
            try
            {
                Files.createDirectories(this.directory);
            }
            catch (IOException e)
            {
                return;
            }
        }

        InputStream source = SkyFortressPlugin.class.getResourceAsStream("/assets/shop.conf");
        try {
            Files.copy(source, this.shop, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
