package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;

public interface QslService {

    List<Qsl> getActiveQslsForLocal(Slot slot);
    
    Qsl save(Qsl qsl);

}
