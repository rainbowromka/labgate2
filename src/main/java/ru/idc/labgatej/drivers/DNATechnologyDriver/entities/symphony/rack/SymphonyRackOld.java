package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.rack;

import lombok.Data;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.rack.SymphonyTestTube;

import java.util.List;

@Data
public class SymphonyRackOld {
    List<SymphonyTestTube> testTubes;
}
