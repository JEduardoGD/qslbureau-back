package egd.fmre.qslbureau.capture.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.controller.MigrationSlotDto;
import egd.fmre.qslbureau.capture.dto.MergeableDataDto;
import egd.fmre.qslbureau.capture.dto.SlotCountqslDTO;
import egd.fmre.qslbureau.capture.dto.SlotDto;
import egd.fmre.qslbureau.capture.dto.jsonburo.BuroDto;
import egd.fmre.qslbureau.capture.entity.CallsignRule;
import egd.fmre.qslbureau.capture.entity.ContactBitacore;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.enums.QslstatusEnum;
import egd.fmre.qslbureau.capture.enums.SlotstatusEnum;
import egd.fmre.qslbureau.capture.exception.MaximumSlotNumberReachedException;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;
import egd.fmre.qslbureau.capture.repo.CallsignruleRepository;
import egd.fmre.qslbureau.capture.repo.LocalRepository;
import egd.fmre.qslbureau.capture.repo.QslRepository;
import egd.fmre.qslbureau.capture.repo.SlotRepository;
import egd.fmre.qslbureau.capture.service.ContactBitacoreService;
import egd.fmre.qslbureau.capture.service.QrzService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.service.WorldBuroesService;
import egd.fmre.qslbureau.capture.util.CompareNacionalityUtil;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;
import egd.fmre.qslbureau.capture.util.QsldtoTransformer;
import egd.fmre.qslbureau.capture.util.RandomUtils;
import egd.fmre.qslbureau.capture.util.SlotsUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SlotLogicServiceImpl extends SlotsUtil implements SlotLogicService {

	@Autowired CallsignruleRepository callsignruleRepository;
	@Autowired SlotRepository slotRepository;
	@Autowired LocalRepository localRepository;
	@Autowired QrzService qrzService;
	@Autowired QslRepository qslRepository;
	@Autowired ContactBitacoreService contactBitacoreService;
	@Autowired WorldBuroesService worldBuroesService;

	//@Value("${MX_PREFIXES}")
	//private String mxPrefixes;

	private static String MAX_NUMBER_SLOTS_REACHED = "Se ha alcanzado el número máximo de slots para este local";

	private Status slotstatusCreated;
	private Status slotstatusOpen;
	private Status slotstatusClosed;
	private Status slotstatusClosedForSend;
	private Status slotstatusSent;
	private Status slotstatusMovedToIntl;
	private Status slotstatusUnconfirmable;
	private Status slotstatusJoined;

	private Status qslStatusVigente;
	private Status qslStatusEliminada;

	@PostConstruct
	private void Init() {
		slotstatusCreated = new Status(SlotstatusEnum.CREATED.getIdstatus());
		slotstatusOpen = new Status(SlotstatusEnum.OPEN.getIdstatus());
		slotstatusClosed = new Status(SlotstatusEnum.CLOSED.getIdstatus());
		slotstatusClosedForSend = new Status(SlotstatusEnum.CLOSED_FOR_SEND.getIdstatus());
		slotstatusSent = new Status(SlotstatusEnum.SENT.getIdstatus());
		slotstatusMovedToIntl = new Status(SlotstatusEnum.MOVED_TO_INTERNATIONAL.getIdstatus());
		slotstatusUnconfirmable = new Status(SlotstatusEnum.UNCONFIRMABLE.getIdstatus());
		slotstatusJoined = new Status(SlotstatusEnum.JOINED.getIdstatus());

		qslStatusVigente = new Status(QslstatusEnum.QSL_VIGENTE.getIdstatus());
		qslStatusEliminada = new Status(QslstatusEnum.QSL_ELIMINADA.getIdstatus());
	}

	@Override
	public List<Slot> getOpenedOrCreatedSlotsInLocal(Local local) {
		List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
		return slotRepository.findByStatusesForLocal(slotStatuses, local);
	}

	@Override
	public List<Slot> getOpenedOrCreatedSlotsForCallsignInLocal(String callsign, Local local) {
		List<Slot> openedOrCreatedSlotsInLocal = getOpenedOrCreatedSlotsInLocal(local);
		return openedOrCreatedSlotsInLocal.stream().filter(s -> callsign.equals(s.getCallsignto()))
				.collect(Collectors.toList());
	}

	@Override
	public List<SlotCountqslDTO> getQslsBySlotIdList(List<Integer> slotIdList) {
		return slotRepository.getQslsBySlotIdList(slotIdList);
	}

	@Override
	public void changeSlotstatusToOpen(Slot slot) {
		Status slotStatus = slot.getStatus();
		if (!slotStatus.getId().equals(slotstatusCreated.getId())) {
			log.warn("The status on slot id {} is not created", slot.getId());
		}
		if (slotStatus.getId().equals(slotstatusOpen.getId())) {
			log.warn("The status on slot id {} already is open", slot.getId());
			return;
		}
		slot.setStatus(slotstatusOpen);
		slotRepository.save(slot);
	}

	@Override
	public Slot changeSlotstatusToClosed(Slot slot, boolean createConfirmCode) {
		Status slotStatus = slot.getStatus();

		if (!slotStatus.getId().equals(slotstatusOpen.getId())) {
			log.warn("The status on slot id {} is not open", slot.getId());
			return slot;
		}
		if (slotStatus.getId().equals(slotstatusClosed.getId())) {
			log.warn("The status on slot id {} already is closed", slot.getId());
			return slot;
		}

		slot.setStatus(slotstatusClosed);
		slot.setClosedAt(DateTimeUtil.getDateTime());

		if (createConfirmCode) {
			slot.setConfirmCode(RandomUtils.randomAlphabetic(6).toUpperCase());
			slot.setStatus(slotstatusClosedForSend);
		}
		return slotRepository.save(slot);
	}

	@Override
	public Slot changeSlotstatusToIntl(Slot slot) {
		Status slotStatus = slot.getStatus();

		if (!slotStatus.getId().equals(slotstatusClosedForSend.getId())) {
			log.warn("The status on slot id {} is not closed for send", slot.getId());
			return slot;
		}
		if (slotStatus.getId().equals(slotstatusMovedToIntl.getId())) {
			log.warn("The status on slot id {} already is moved to international", slot.getId());
			return slot;
		}

		slot.setStatus(slotstatusMovedToIntl);
		slot.setClosedAt(DateTimeUtil.getDateTime());

		return slotRepository.save(slot);
	}

	@Override
	public Slot changeSlotstatusToUnconfirmable(Slot slot) {
		Status slotStatus = slot.getStatus();

		if (!slotStatus.getId().equals(slotstatusClosedForSend.getId())) {
			log.warn("The status on slot id {} is not closed for send", slot.getId());
			return slot;
		}
		if (slotStatus.getId().equals(slotstatusUnconfirmable.getId())) {
			log.warn("The status on slot id {} already marked as unconfirmable", slot.getId());
			return slot;
		}

		slot.setStatus(slotstatusUnconfirmable);
		slot.setClosedAt(DateTimeUtil.getDateTime());

		return slotRepository.save(slot);
	}

	@Override
	public Slot getSlotByCountry(String callsign, Local local) throws MaximumSlotNumberReachedException {
		List<BuroDto> buroesOfCallsign;
		try {
		    buroesOfCallsign = worldBuroesService.findByCallsign(callsign);
		} catch (WorldBuroesServiceImplException e) {
		    log.error(e.getMessage());
		    return null;
		}

		String country = null;
		if (!buroesOfCallsign.isEmpty()) {
		    country = buroesOfCallsign.get(0).getPais();
		}

		if (country == null) {
			return null;
		}
		List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
		List<Slot> slots = slotRepository.findByLocalAndCountryAndStatuses(country, slotStatuses, local);

		if (slots != null && !slots.isEmpty()) {
			return slots.stream().sorted(Comparator.comparingInt(Slot::getId).reversed()).findFirst().get();
		}

		List<Slot> openedOrCreatedSlotsInLocal = getOpenedOrCreatedSlotsInLocal(local);

		if (openedOrCreatedSlotsInLocal.size() <= 0 && local.getMaxSlots() > 0) {
			Slot newSlot = generateSlotCountry(country, DateTimeUtil.getDateTime(), local, 1);
			return slotRepository.save(newSlot);
		}
		openedOrCreatedSlotsInLocal.sort(Comparator.comparing(Slot::getSlotNumber));

		if (openedOrCreatedSlotsInLocal.size() >= local.getMaxSlots()) {
			throw new MaximumSlotNumberReachedException(MAX_NUMBER_SLOTS_REACHED);
		}

		int i = 1;
		for (Slot s : openedOrCreatedSlotsInLocal) {
			if (i == s.getSlotNumber()) {
				i++;
				continue;
			} else {
				Slot newSlot = generateSlotCountry(country, DateTimeUtil.getDateTime(), local, i);
				return slotRepository.save(newSlot);
			}
		}
		Slot newSlot = generateSlotCountry(country, DateTimeUtil.getDateTime(), local, i);
		return slotRepository.save(newSlot);
	}

	@Override
	public void runCloseCloseableSlots(Local local) {
		List<Integer> closeableSlotIds = slotRepository.gettingCloseableSlotIds(local.getId());
		List<Slot> closeableList = closeableSlotIds.stream().map(slotId -> slotRepository.findById(slotId).get())
				.collect(Collectors.toList());
		closeableList.forEach(slot -> this.changeSlotstatusToClosed(slot, false));
	}

	@Override
	public void runOpenOpenableSlots(Local local) {
		List<Integer> opneableSlotIds = slotRepository.gettingOpenableSlotIds(local.getId());
		List<Slot> opneableSlots = opneableSlotIds.stream().map(slotId -> slotRepository.findById(slotId).get())
				.collect(Collectors.toList());
		opneableSlots.forEach(slot -> this.changeSlotstatusToOpen(slot));
	}

	private String applyCallsignRule(String callsignTo) {
		// get applicable rules for callsignTo
		List<CallsignRule> callsignRules = callsignruleRepository.getActiveRulesForCallsign(DateTimeUtil.getDateTime(),
				callsignTo);

		if (callsignRules.size() <= 0) {
			return callsignTo;
		}

		// filter an apply only last rule
		CallsignRule aplicableRule = callsignRules.stream().filter(c -> isOntime.test(c, DateTimeUtil.getDateTime()))
				.sorted(Comparator.comparingInt(CallsignRule::getId)).reduce((f, s) -> s).orElse(null);

		return aplicableRule == null ? callsignTo : aplicableRule.getCallsignRedirect();
	}

	@Override
	public Slot getSlotForQsl(String callsignTo, Local local) throws MaximumSlotNumberReachedException {
		// apply rules of redirect
		String newCallsignTo = this.applyCallsignRule(callsignTo);

		// find some slot open or created that is used by newCallsignTo
		List<Slot> openedOrCreatedSlotsForCallsignInLocal = getOpenedOrCreatedSlotsForCallsignInLocal(newCallsignTo,
				local);
		if (openedOrCreatedSlotsForCallsignInLocal != null && !openedOrCreatedSlotsForCallsignInLocal.isEmpty()) {
			return openedOrCreatedSlotsForCallsignInLocal.get(0);
		}

		List<Slot> openedOrCreatedSlotsInLocal = getOpenedOrCreatedSlotsInLocal(local);

		if (openedOrCreatedSlotsInLocal.size() <= 0 && local.getMaxSlots() > 0) {
			Slot newSlot = generateSlot(newCallsignTo, DateTimeUtil.getDateTime(), local, 1);
			return slotRepository.save(newSlot);
		}
		openedOrCreatedSlotsInLocal.sort(Comparator.comparing(Slot::getSlotNumber));

		if (openedOrCreatedSlotsInLocal.size() >= local.getMaxSlots()) {
			throw new MaximumSlotNumberReachedException(MAX_NUMBER_SLOTS_REACHED);
		}

		int i = 1;
		for (Slot s : openedOrCreatedSlotsInLocal) {
			if (i == s.getSlotNumber()) {
				i++;
				continue;
			} else {
				Slot newSlot = generateSlot(newCallsignTo, DateTimeUtil.getDateTime(), local, i);
				return slotRepository.save(newSlot);
			}
		}
		Slot newSlot = generateSlot(newCallsignTo, DateTimeUtil.getDateTime(), local, i);
		return slotRepository.save(newSlot);
	}

	@Override
	public Slot findById(int slotId) {
		return slotRepository.findById(slotId).orElse(null);
	}

	@Override
	public List<Status> getCreatedAndOpenStatuses() {
		return Arrays.asList(slotstatusCreated, slotstatusOpen);
	}

	@Override
	public List<Slot> getOpenedOrCreatedSlots() {
		List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
		return slotRepository.findByStatusesForLocal(slotStatuses);
	}

	@Override
	public List<Slot> getSlotsOfLocal(Local local) {
		return slotRepository.findByLocal(local);
	}

	@Override
	public List<Slot> orderAndFilterForFront(List<Slot> slots) {
		return orderAndFilter(slots);
	}

	@Override
	public List<Slot> orderAndFilterReadyForSend(List<Slot> slots) {
		return orderAndFilter(slots, Arrays.asList(slotstatusClosedForSend, slotstatusSent));
	}

	@Override
	public Slot changeSlotstatusToSend(Slot slot) {
		Status slotStatus = slot.getStatus();
		if (!slotStatus.getId().equals(slotstatusClosedForSend.getId())) {
			log.warn("The status on slot id {} is not closed for send", slot.getId());
			return null;
		}
		if (slotStatus.getId().equals(slotstatusSent.getId())) {
			log.warn("The status on slot id {} already is on sent status", slot.getId());
			return null;
		}

		slot.setClosedAt(DateTimeUtil.getDateTime());
		slot.setStatus(slotstatusSent);
		return slotRepository.save(slot);
	}

	@Override
	public Slot getNullSlot() {
		List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
		List<Slot> slots = slotRepository.getNullSlots(slotStatuses);
		if (slots != null && !slots.isEmpty()) {
			return slots.get(StaticValuesHelper.ZERO);
		}
		return null;
	}

	@Override
	@Transactional
	public SlotDto migrateSlot(MigrationSlotDto migrationSlotDto) throws QslcaptureException {
		Local local = localRepository.findById(migrationSlotDto.getNewlocalid()).orElse(null);

		Slot oldSlot = slotRepository.findById(migrationSlotDto.getSlotid()).orElse(null);
		List<ContactBitacore> contactBitacoreList = contactBitacoreService.findEntityBySlot(oldSlot);
		
		List<Qsl> qsls = qslRepository.findBySlotAndStatuses(oldSlot,
				Arrays.asList(qslStatusVigente, qslStatusEliminada));
		Slot slot = null;
		for (Qsl qsl : qsls) {
			String effectiveCallsign = qsl.getVia() != null && !StaticValuesHelper.EMPTY_STRING.equals(qsl.getVia())
					? qsl.getVia()
					: qsl.getTo();
			
			List<BuroDto> buroDtoList;
			    try {
				buroDtoList = worldBuroesService.findByCallsign(effectiveCallsign);
			    } catch (WorldBuroesServiceImplException e) {
				throw new QslcaptureException(e);
			    }
		            
			    if (buroDtoList == null || buroDtoList.isEmpty()) {
				log.error("No se encontraron datos de buro para el callsign: " + effectiveCallsign);
				throw new QslcaptureException("No se encontraron datos de buro para el callsign: " + effectiveCallsign);
			    }
			    
			boolean isMexican = CompareNacionalityUtil.isMexican(effectiveCallsign, buroDtoList);

			try {
				if (isMexican) {
					slot = getSlotForQsl(effectiveCallsign, local);
				} else {
					slot = getSlotByCountry(effectiveCallsign, local);
				}
			} catch (MaximumSlotNumberReachedException e) {
				throw new QslcaptureException(e);
			}

			if (slot == null) {
				slot = getNullSlot();
			}

			if (slot == null) {
				throw new QslcaptureException("No pudo generarse el slot");
			}
			
			contactBitacoreService.migrateContactBitacore(contactBitacoreList, slot);

			changeSlotstatusToOpen(slot);
			qsl.setSlot(slot);
			qsl = qslRepository.save(qsl);
		}

		changeSlotstatusToClosed(oldSlot, false);

		return QsldtoTransformer.map(slot);
	}

	@Override
	public List<Slot> getOpenedOrCreatedSlotByCallsign(String calssingTo) {
		List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
		List<Slot> slots = slotRepository.getSlotByCallsignAndStatuses(calssingTo, slotStatuses);
		if (slots == null || slots.isEmpty()) {
			return null;
		}
		if (slots.size() > 1) {
			Set<Integer> localsIds = slots.stream().map(s -> s.getLocal().getId()).collect(Collectors.toSet());
			if (slots.size() != localsIds.size()) {
				log.error("Error al consultar los slots para el callsign {}", calssingTo);
				return null;
			}
		}
		return slots;
	}

	@Override
	public List<MergeableDataDto> getApplicableTraslates(int localid) {
		List<MergeableDataDto> mergeableDataDtoList = new ArrayList<>();
		
		Local local = localRepository.findById(localid);
		List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
		List<Slot> slotForLocalList = slotRepository.findByStatusesForLocal(slotStatuses, local);
		List<CallsignRule> callsignRuleList = callsignruleRepository.getActiveRules(DateTimeUtil.getDateTime());
		for (CallsignRule callsignRule : callsignRuleList) {
			Slot slotOne = slotForLocalList.stream()
					.filter(slot -> slot.getCallsignto()!= null && slot.getCallsignto().equals(callsignRule.getCallsignTo()))
					.findFirst()
					.orElse(null);
			Slot slotTwo = slotForLocalList.stream()
					.filter(slot -> slot.getCallsignto()!= null && slot.getCallsignto().equals(callsignRule.getCallsignRedirect()))
					.findFirst()
					.orElse(null);
			if (slotOne != null && slotTwo != null && slotOne.getId().compareTo(slotTwo.getId()) != 0) {
				SlotDto slotOneDto = QsldtoTransformer.map(slotOne);
				SlotDto slotTwoDto = QsldtoTransformer.map(slotTwo);
				MergeableDataDto mergeableDataDto = new MergeableDataDto();
				mergeableDataDto.setSlotOrigen(slotOneDto);
				mergeableDataDto.setSlotDestino(slotTwoDto);
				mergeableDataDtoList.add(mergeableDataDto);
			}
		}
		return mergeableDataDtoList;
	}

	@Override
	@Transactional
	public boolean mergeChanges(MergeableDataDto mergeableDataDto) {
 		int origenSlotId = mergeableDataDto.getSlotOrigen().getSlotId();
		Slot origenSlot = slotRepository.findById(origenSlotId).orElse(null);
		
		List<ContactBitacore> contactBitacoreList = contactBitacoreService.findEntityBySlot(origenSlot);
		
		int destinoSlotId = mergeableDataDto.getSlotDestino().getSlotId();
		Slot destinoSlot = slotRepository.findById(destinoSlotId).orElse(null);
		
		contactBitacoreService.migrateContactBitacore(contactBitacoreList, destinoSlot);
		
		Set<Qsl> qslsSlotOrigen = qslRepository.findBySlot(origenSlot);
		List<Qsl> qslsInNewSlot = qslsSlotOrigen.stream().map(qsl -> {
			qsl.setSlot(destinoSlot);
			return qsl;
		}).collect(Collectors.toList());
		qslRepository.saveAll(qslsInNewSlot);
		
		origenSlot.setStatus(slotstatusJoined);
		slotRepository.save(origenSlot);
		
		return true;
	}
}

