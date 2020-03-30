var PetsView = (function() {
	var dao;
	var daoPeople
	// Referencia a this que permite acceder a las funciones públicas desde las
	// funciones de jQuery.
	var self;

	var formId = 'pets-form';
	var listId = 'pets-list';
	var formQuery = '#' + formId;
	var listQuery = '#' + listId;

	function PetsView(petsDao, peopleDao, formContainerId, listContainerId) {
		dao = petsDao;
		daoPeople = peopleDao;
		self = this;

		insertPetsTitulo($('#' + formContainerId));
		insertPetsForm($('#' + formContainerId));
		insertPetsList($('#' + listContainerId));

		this.init = function() {
			dao.listPets(function(pets) {
				$.each(pets, function(key, pet) {
					appendToTable(pet);
				});
			}, function() {
				alert('No ha sido posible acceder al listado de mascotas.');
			});

			// La acción por defecto de enviar formulario (submit) se
			// sobreescribe
			// para que el envío sea a través de AJAX
			$(formQuery).submit(function(event) {
				var pet = self.getPetInForm();

				if (self.isEditing()) {
					dao.modifyPet(pet, function(pet) {
						$('#pet-' + pet.id + ' td.name').text(pet.name);
						// $('#pet-' + pet.id + ' td.owner').val(pet.owner);
						self.addOwnerToPet(pet);
						self.resetForm();
					}, showErrorMessage, self.enableForm);
				} else {
					dao.addPet(pet, function(pet) {
						appendToTable(pet);
						self.resetForm();
					}, showErrorMessage, self.enableForm);
				}

				return false;
			});

			$('#btnClear').click(this.resetForm);
		};

		this.getPetInForm = function() {
			var form = $(formQuery);
			return {
				'id' : form.find('input[name="id"]').val(),
				'name' : form.find('input[name="name"]').val(),
				'owner' : form.find('select[name="owner"]').val()
			};
		};

		this.getPetInRow = function(id) {
			var row = $('#pet-' + id);

			if (row !== undefined) {
				return {
					'id' : id,
					'name' : row.find('td.name').text(),
					'owner' : row.find('td.owner').text()
				};
			} else {
				return undefined;
			}
		};

		this.editPet = function(id) {
			var row = $('#pet-' + id);

			if (row !== undefined) {
				var form = $(formQuery);

				form.find('input[name="id"]').val(id);
				form.find('input[name="name"]').val(row.find('td.name').text());
				dao.getPet(id, function(pet) {
					form.find('select[name="owner"]').val(pet.owner);
				});
				$('input#btnSubmit').val('Modificar');
			}

		};

		this.addOwnerToPet = function(pet) {
			var petId = pet.id;
			daoPeople.getPerson(pet.owner, function(owner) {
				$('#pet-' + petId + ' td.owner').text(
						owner.name + ' ' + owner.surname);
			});
		};

		this.deletePet = function(id) {
			if (confirm('Está a punto de eliminar a una mascota. ¿Está seguro de que desea continuar?')) {
				dao.deletePet(id, function() {
					$('tr#pet-' + id).remove();
				}, showErrorMessage);
			}
		};

		this.isEditing = function() {
			return $(formQuery + ' input[name="id"]').val() != "";
		};

		this.disableForm = function() {
			$(formQuery + ' input').prop('disabled', true);
		};

		this.enableForm = function() {
			$(formQuery + ' input').prop('disabled', false);
		};

		this.resetForm = function() {
			$(formQuery)[0].reset();
			$(formQuery + ' input[name="id"]').val('');
			$('#btnSubmit').val('Crear');
		};
	}
	;

	var insertPetsList = function(parent) {
		parent
				.append('<table id="'
						+ listId
						+ '" class="table">\
				<thead>\
					<tr class="row">\
						<th class="col-sm-4">Nombre</th>\
						<th class="col-sm-5">Propietario</th>\
						<th class="col-sm-3">&nbsp;</th>\
					</tr>\
				</thead>\
				<tbody>\
				</tbody>\
			</table>');
	};

	var insertPetsTitulo = function(parent) {
		parent.append('<h1 class="display-5 mt-3 mb-3">Mascotas\</h1>');
	}

	var insertPetsForm = function(parent) {
		parent
				.append('<form id="'
						+ formId
						+ '" class="mb-5 mb-10">\
				<input name="id" type="hidden" value=""/>\
				<div class="row">\
					<div class="col-sm-4">\
						<input name="name" type="text" value="" placeholder="Nombre" class="form-control" required/>\
					</div>\
					<div class="col-sm-5">\
						\<select name="owner" id="personas-select" class="form-control" required>\</select>\
					</div>\
					<div class="col-sm-3">\
						<input id="btnSubmit" type="submit" value="Crear" class="btn btn-primary" />\
						<input id="btnClear" type="reset" value="Limpiar" class="btn" />\
					</div>\
				</div>\
			</form>');

		daoPeople.listPeople(function(people) {
			$.each(people, function(key, person) {
				$("#personas-select").append(
						'\<option name="' + person.id + '" id="' + person.id
								+ '" value="' + person.id + '">' + person.name
								+ ' ' + person.surname + '\</option>')
			});
		}, function() {
			alert('No ha sido posible acceder al listado de personas.');
		});

	};

	var createPetRow = function(pet) {
		return '<tr id="pet-'
				+ pet.id
				+ '" class="row">\
			<td class="name col-sm-4">'
				+ pet.name
				+ '</td>\
			<td class="owner col-sm-5">\
				</td>\
			<td class="col-sm-3">\
				<a class="edit btn btn-primary" href="#">Editar</a>\
				<a class="delete btn btn-warning" href="#">Eliminar</a>\
			</td>\
		</tr>';
	};

	var showErrorMessage = function(jqxhr, textStatus, error) {
		alert(textStatus + ": " + error);
	};

	var addRowListeners = function(pet) {
		$('#pet-' + pet.id + ' a.edit').click(function() {
			self.editPet(pet.id);
		});

		$('#pet-' + pet.id + ' a.delete').click(function() {
			self.deletePet(pet.id);
		});
	};

	var appendToTable = function(pet) {
		$(listQuery + ' > tbody:last').append(createPetRow(pet));
		self.addOwnerToPet(pet);
		addRowListeners(pet);
	};

	return PetsView;
})();
