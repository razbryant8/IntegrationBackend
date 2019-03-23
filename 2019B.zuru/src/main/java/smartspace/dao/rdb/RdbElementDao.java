package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import smartspace.dao.ElementDao;
import smartspace.data.ElementEntity;


@Repository
public class RdbElementDao implements ElementDao<String> {

	private EntityCrud entityCrud;
	private AtomicLong nextMessageId;
	
	@Autowired
	public RdbElementDao(EntityCrud entityCrud) {
		this.entityCrud=entityCrud;
		this.nextMessageId=new AtomicLong(1L);
	}
	
	@Override
	@Transactional
	public ElementEntity create(ElementEntity elementEntity) {
		elementEntity.setElementId(""+nextMessageId.getAndIncrement()+elementEntity.getElementSmartspace());
		return this.entityCrud.save(elementEntity);
	}

	@Override
	@Transactional
	public Optional<ElementEntity> readById(String elementKey) {
		return this.entityCrud.findById(elementKey);
	}

	@Override
	@Transactional
	public List<ElementEntity> readAll() {
		List<ElementEntity> entityList = new ArrayList<>();
		this.entityCrud.findAll().forEach(entity->entityList.add(entity));
		return entityList;
	}

	@Override
	@Transactional
	public void update(ElementEntity elementEntity) {
		if (this.entityCrud.existsById(elementEntity.getElementId())) {
			this.entityCrud.save(elementEntity);
		}else {
			throw new RuntimeException("no element with id: " + elementEntity.getElementId());
		}
		
	}

	@Override
	@Transactional
	public void deleteByKey(String elementKey) {
		this.entityCrud.deleteById(elementKey);
		
	}

	@Override
	@Transactional
	public void delete(ElementEntity elementEntity) {
		this.entityCrud.delete(elementEntity);
		
	}

	@Override
	@Transactional
	public void deleteAll() {
		this.entityCrud.deleteAll();
		
	}

}
