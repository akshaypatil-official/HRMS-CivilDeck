document.addEventListener("DOMContentLoaded", function() {
    const dateField = document.getElementById('todayDate');
    
    if (dateField) {
        const today = new Date();
        
        // 1. Calculate Yesterday (Last 1 Day)
        const yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);
        
        // 2. Format both dates to YYYY-MM-DD
        const formatDate = (d) => {
            const year = d.getFullYear();
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const day = String(d.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        };
        
        const todayStr = formatDate(today);
        const yesterdayStr = formatDate(yesterday);
        
        // 3. Set constraints: Min date is yesterday, Max date is today
        dateField.min = yesterdayStr;
        dateField.max = todayStr;
        
        // 4. Default to Current Date if the field is empty (New Entry)
        if (!dateField.value) {
            dateField.value = todayStr;
        }
    }

    // Trigger history calculation function
    if (typeof calculateHistory === "function") {
        calculateHistory();
    }
});

function setCurrentTime(fieldId) {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    
    document.getElementById(fieldId).value = `${hours}:${minutes}`;
    
    if (typeof calculateFormDuration === "function") {
        calculateFormDuration();
    }
}


        function calculateFormDuration() {
            const inVal = document.getElementById('timeIn').value;
            const outVal = document.getElementById('timeOut').value;
            const display = document.getElementById('currentSessionHrs');
            if (inVal && outVal) {
                const [h1, m1] = inVal.split(':').map(Number);
                const [h2, m2] = outVal.split(':').map(Number);
                let startMinutes = (h1 * 60) + m1;
                let endMinutes = (h2 * 60) + m2;
                let diffMinutes = endMinutes - startMinutes;
                if (diffMinutes < 0) diffMinutes += 1440; 
                display.textContent = formatDuration(diffMinutes);
            }
        }

        function calculateHistory() {
            let grandTotalMinutes = 0;
            document.querySelectorAll('#historyTable tbody tr').forEach(row => {
                const inEl = row.querySelector('.t-in');
                const outEl = row.querySelector('.t-out');
                const totalEl = row.querySelector('.row-total');
                if (inEl && outEl && inEl.textContent.trim() !== "") {
                    const minutes = getDiffMinutes(inEl.textContent, outEl.textContent);
                    totalEl.textContent = formatDuration(minutes);
                    grandTotalMinutes += minutes;
                }
            });
            const grandTotalEl = document.getElementById('grand-total');
            if(grandTotalEl) {
                grandTotalEl.textContent = (grandTotalMinutes / 60).toFixed(2);
            }
        }

        function formatDuration(totalMinutes) {

            const hrs = Math.floor(totalMinutes / 60);

            const mins = totalMinutes % 60;

            if (hrs === 0) {

                return mins + " min";
            }

            if (mins === 0) {

                return hrs + " hrs";
            }

            return hrs + " hrs " + mins + " min";
        }

        function getDiffMinutes(startStr, endStr) {
            const parse = (str) => {
                const match = str.match(/(\d+):(\d+)\s*(AM|PM)/i);
                if (!match) return 0;
                let h = parseInt(match[1]);
                const m = parseInt(match[2]);
                const ampm = match[3].toUpperCase();
                if (ampm === 'PM' && h !== 12) h += 12;
                if (ampm === 'AM' && h === 12) h = 0;
                return (h * 60) + m;
            };
            let diff = parse(endStr) - parse(startStr);
            return diff < 0 ? diff + 1440 : diff;
        }
        
        function fetchCurrentLocation() {

            const input =
                document.getElementById('userLocation');

            if (navigator.geolocation) {

                input.placeholder = "Locating...";

                navigator.geolocation.getCurrentPosition(

                    (position) => {

                        input.value =
                            position.coords.latitude
                            .toFixed(4)
                            + ", " +
                            position.coords.longitude
                            .toFixed(4);
                    },

                    () => {

                        input.placeholder =
                            "Office / Remote";
                    }
                );
            }
        }
      
        function openPhotoModal(button) {
            // Get the image URL from the clicked button's data attribute
            const photoUrl = button.getAttribute('data-photo-url');
            
            // Update modal elements
            document.getElementById('modalTargetImg').src = photoUrl;
            document.getElementById('modalDownloadBtn').href = photoUrl;
            
            // Show the modal
            document.getElementById('photoModal').style.display = 'flex';
        }

        function closePhotoModal() {
            // Hide the modal
            document.getElementById('photoModal').style.display = 'none';
        }

        // Close modal if user clicks outside the white content box
        window.onclick = function(event) {
            const modal = document.getElementById('photoModal');
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        }
		
		
		
		function handleStatusChange() {
		    const statusSelect = document.getElementById('statusSelect');
		    if (!statusSelect) return;
		    
		    const status = statusSelect.value;
		    
		    const morningGroup = document.getElementById('morningTimeGroup');
		    const nightGroup = document.getElementById('nightTimeGroup');
		    
		    const timeInField = document.getElementById('timeIn');
		    const timeOutField = document.getElementById('timeOut');
		    const timeInBtn = document.getElementById('timeInBtn');
		    const timeOutBtn = document.getElementById('timeOutBtn');
		    
		    const nightInField = document.getElementById('nightTimeIn');
		    const nightOutField = document.getElementById('nightTimeOut');
		    const nightInBtn = document.getElementById('btnNightIn');
		    const nightOutBtn = document.getElementById('btnNightOut');
		    const hiddenNightStatus = document.getElementById('nightStatusInput');

		    const now = new Date();
		    const currentHour = now.getHours();

		    if (status === 'DayNight') {
		        // Validation: If it is before 6:00 PM (18:00) and after 4:00 AM
		        if (currentHour < 18 && currentHour >= 4) {
		            alert("Night shift actions are only allowed after 06:00 PM.");
		            statusSelect.value = 'Present'; // Revert selection back to Present
		            handleStatusChange(); // Re-run to fix UI states
		            return;
		        }

		        if (morningGroup) morningGroup.style.display = 'none';
		        if (timeInBtn) timeInBtn.disabled = true;
		        if (timeOutBtn) timeOutBtn.disabled = true;
		        if (timeInField) timeInField.value = '';
		        if (timeOutField) timeOutField.value = '';

		        if (nightGroup) nightGroup.style.display = 'flex';
		        if (nightInBtn) nightInBtn.disabled = false;
		        if (nightOutBtn) nightOutBtn.disabled = false;
		    } 
		    else if (status === 'Present') {
		        if (morningGroup) morningGroup.style.display = 'flex';
		        if (timeInBtn) timeInBtn.disabled = false;
		        if (timeOutBtn) timeOutBtn.disabled = false;

		        if (nightGroup) nightGroup.style.display = 'none';
		        if (nightInBtn) nightInBtn.disabled = true;
		        if (nightOutBtn) nightOutBtn.disabled = true;
		        if (nightInField) nightInField.value = '';
		        if (nightOutField) nightOutField.value = '';
		        if (hiddenNightStatus) hiddenNightStatus.value = ''; 
		    } 
		    else if (status === 'Absent' || status === 'Holiday') {
		        if (morningGroup) morningGroup.style.display = 'none';
		        if (nightGroup) nightGroup.style.display = 'none';
		        
		        if (timeInBtn) timeInBtn.disabled = true;
		        if (timeOutBtn) timeOutBtn.disabled = true;
		        if (nightInBtn) nightInBtn.disabled = true;
		        if (nightOutBtn) nightOutBtn.disabled = true;
		        
		        if (timeInField) timeInField.value = '';
		        if (timeOutField) timeOutField.value = '';
		        if (nightInField) nightInField.value = '';
		        if (nightOutField) nightOutField.value = '';
		        if (hiddenNightStatus) hiddenNightStatus.value = ''; 
		    }
		}

		function checkTimeAndSetDefaultStatus() {
		    const statusSelect = document.getElementById('statusSelect');
		    if (!statusSelect) return;

		    const now = new Date();
		    const currentHour = now.getHours();

		    if (currentHour >= 18 || currentHour < 4) {
		        statusSelect.value = 'DayNight';
		    } else {
		        statusSelect.value = 'Present'; 
		    }
		    
		    handleStatusChange();
		}

		function setCurrentTime(inputId) {
		    const now = new Date();
		    const hours = String(now.getHours()).padStart(2, '0');
		    const minutes = String(now.getMinutes()).padStart(2, '0');
		    const timeString = `${hours}:${minutes}`;
		    
		    const inputField = document.getElementById(inputId);
		    if (inputField) {
		        inputField.value = timeString;
		    }

		    // NEW: Disable the button that was just clicked
		    if (inputId === 'timeIn') {
		        const timeInBtn = document.getElementById('timeInBtn');
		        if (timeInBtn) timeInBtn.disabled = true;

		        const btnNightIn = document.getElementById('btnNightIn');
		        if (btnNightIn) btnNightIn.disabled = true;
		    }
		    
		    // NEW: Disable the night In button when night time is filled
		    if (inputId === 'nightTimeIn') {
		        const btnNightIn = document.getElementById('btnNightIn');
		        if (btnNightIn) btnNightIn.disabled = true;
		    }

		    if (inputId === 'nightTimeOut') {
		        calculateNightStatus(hours, minutes);
		    }
		}


		function calculateNightStatus(hours, minutes) {
		    const currentHour = parseInt(hours, 10);
		    const currentMinute = parseInt(minutes, 10);
		    
		    const hiddenNightStatus = document.getElementById('nightStatusInput');
		    const selectElem = document.getElementById('statusSelect');
		    const nightOption = selectElem ? selectElem.querySelector('option[value="DayNight"]') : null;

		    let statusText = "";

		    // 1:00 AM (inclusive) to 4:00 AM (exclusive)
		    if (currentHour >= 1 && currentHour < 4) {
		        statusText = "Full Night";
		    } 
		    // 11:00 PM (23:00) up to 1:00 AM (00:59)
		    else if (currentHour === 23 || currentHour === 0) {
		        statusText = "Half Night";
		    } 
		    // Before 11:00 PM (23:00)
		    else {
		        statusText = "Short Night"; 
		    }

		    if (hiddenNightStatus) hiddenNightStatus.value = statusText; 
		    if (nightOption) nightOption.textContent = `Night (${statusText})`;
		}


		// AUTOMATIC INTERCEPTOR: Listens for the form submit and converts DayNight to Present
		document.addEventListener("DOMContentLoaded", function() {
		    checkTimeAndSetDefaultStatus(); 
		    
		    const statusSelect = document.getElementById('statusSelect');
		    if (statusSelect) {
		        statusSelect.addEventListener('change', handleStatusChange);
		        
		        // Find the parent HTML form element
		        const parentForm = statusSelect.closest('form');
		        if (parentForm) {
		            parentForm.addEventListener('submit', function() {
		                // If the user selects Night (DayNight), force it to Present right before saving
		                if (statusSelect.value === 'DayNight') {
		                    statusSelect.value = 'Present';
		                }
		            });
		        }
		    }
		});

		
		
		