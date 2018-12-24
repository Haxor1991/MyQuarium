package br.com.myaquarium.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.myaquarium.enums.AquariumCicle;
import br.com.myaquarium.model.Aquarium;
import br.com.myaquarium.model.AquariumData;
import br.com.myaquarium.model.User;
import br.com.myaquarium.repository.AquariumRepository;
import br.com.myaquarium.validations.AquariumValidations;

@Service
public class AquariumService {

	@Autowired
	private AquariumRepository aquariumRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private AquariumDataService aquariumDataService;

	public void saveNewAquarium(String aquariumName, String aquariumEndpoint, User user, Double temperature,
			AquariumCicle cicle) throws Exception {
		new AquariumValidations(aquariumName, aquariumEndpoint, aquariumRepository, temperature).makeValidations();
		Aquarium aquarium = new Aquarium(aquariumName, aquariumEndpoint, user, temperature, cicle);
		aquariumRepository.save(aquarium);
		Collection<Aquarium> aquariumList = user.getAquariumList();
		if (aquariumList != null) {
			aquariumList.add(aquarium);
			userService.saveUser(user);
		} else {
			HashSet<Aquarium> aquariumSet = new HashSet<Aquarium>();
			aquariumSet.add(aquarium);
			user.setAquariumList(aquariumSet);
			userService.saveUser(user);
		}
	}

	public Aquarium getAquariumByEndpoint(String endpoint) {
		return aquariumRepository.findByAquariumEndpoint(endpoint);
	}

	public List<Aquarium> getAllAquariuns() {
		Iterable<Aquarium> aquariumIterator = aquariumRepository.findAll();
		ArrayList<Aquarium> aquariumList = new ArrayList<>();
		aquariumIterator.forEach(aquariumList::add);

		return aquariumList;
	}

	public void saveAquarium(Aquarium aquarium) {
		aquariumRepository.save(aquarium);
	}

	public Aquarium getAquariumById(Long aquariumId) {
		return aquariumRepository.findById(aquariumId);
	}

	public void deleteAquarium(Aquarium aquarium) {
		Collection<AquariumData> aquariumData = aquarium.getAquariumData();
		if (aquariumData != null && aquariumData.size() > 0) {
			aquariumData.forEach(data -> aquariumDataService.deleteAquariumData(data));
		}
		aquariumRepository.delete(aquarium);
	}

	public User deleteAquarium(Long aquarium) {
		Aquarium aq = aquariumRepository.findById(aquarium);
		if (aq != null) {
			Collection<AquariumData> aquariumData = aq.getAquariumData();
			if (aquariumData != null && aquariumData.size() > 0) {
				aquariumData.forEach(data -> aquariumDataService.deleteAquariumData(data));
			}
			User user = aq.getUser();
			user.getAquariumList().remove(aq);
			aquariumRepository.delete(aq);
			userService.saveUser(user);

			return user;
		}

		return null;
	}

}
