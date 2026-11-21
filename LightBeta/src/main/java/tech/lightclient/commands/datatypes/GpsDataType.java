package tech.lightclient.commands.datatypes;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.config.GPSStorage;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.commands.helpers.TabCompleteHelper;

import java.util.List;
import java.util.stream.Stream;

public enum GpsDataType implements IDatatypeFor<GPSStorage.GPSPoint> {
    INSTANCE;

    @Override
    public Stream<String> tabComplete(IDatatypeContext datatypeContext) throws CommandException {
        Stream<String> ways = getWay().stream().map(GPSStorage.GPSPoint::getName);
        String context = datatypeContext.getConsumer().getString();
        return new TabCompleteHelper().append(ways).filterPrefix(context).sortAlphabetically().stream();
    }

    @Override
    public GPSStorage.GPSPoint get(IDatatypeContext datatypeContext) throws CommandException {
        String text = datatypeContext.getConsumer().getString();
        return getWay().stream().filter(s -> s.getName().equalsIgnoreCase(text)).findFirst().orElse(null);
    }

    private List<? extends GPSStorage.GPSPoint> getWay() {
        return LightClient.getGpsStorage().getPoints();
    }
}

