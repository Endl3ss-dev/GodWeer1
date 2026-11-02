package tech.lightclient.commands.datatypes;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.config.StaffStorage;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.commands.helpers.TabCompleteHelper;

import java.util.List;
import java.util.stream.Stream;

public enum StaffDataType implements IDatatypeFor<StaffStorage.Staff> {
    INSTANCE;

    @Override
    public Stream<String> tabComplete(IDatatypeContext datatypeContext) throws CommandException {
        Stream<String> ways = getStaff().stream().map(StaffStorage.Staff::getName);
        String context = datatypeContext.getConsumer().getString();
        return new TabCompleteHelper().append(ways).filterPrefix(context).sortAlphabetically().stream();
    }

    @Override
    public StaffStorage.Staff get(IDatatypeContext datatypeContext) throws CommandException {
        String text = datatypeContext.getConsumer().getString();
        return getStaff().stream().filter(s -> s.getName().equalsIgnoreCase(text)).findFirst().orElse(null);
    }

    private List<? extends StaffStorage.Staff> getStaff() {
        return LightClient.getStaffStorage().getStaffs();
    }
}

