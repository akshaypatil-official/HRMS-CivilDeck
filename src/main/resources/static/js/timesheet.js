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

		    // 1. Get the Date Input element securely
		    const dateInput = document.getElementById('todayDate'); 
		    
		    const now = new Date();
		    
		    // Create a local Date Object for real-world TODAY at midnight
		    const currentTodayDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());

		    let selectedDateObject = null;
		    let isTodaySelected = false;

		    // 2. Format-Agnostic extraction engine with integrated live console logs
		    if (dateInput && dateInput.value) {
		        const rawDateString = dateInput.value; 
		        console.log("Raw Selected Date String from Input: " + rawDateString);

		        const parts = rawDateString.split('-'); // Splits standard HTML5 string "YYYY-MM-DD"
		        if (parts.length === 3) {
		            const year = parseInt(parts[0], 10);
		            const month = parseInt(parts[1], 10) - 1; // JS months run 0-11 index mapping
		            const day = parseInt(parts[2], 10);
		            
		            // Generate clean native instance representing selected date
		            selectedDateObject = new Date(year, month, day);
		        }
		    } else {
		        console.log("No date is currently selected in the input box.");
		    }

		    // 3. Compare timestamps and print match results to console
		    if (selectedDateObject && !isNaN(selectedDateObject.getTime())) {
		        isTodaySelected = (selectedDateObject.getTime() === currentTodayDate.getTime());
		        
		        console.log("Parsed Local Date Object: " + selectedDateObject.toString());
		        console.log("Extracted Day Number: " + selectedDateObject.getDate());
		        console.log("Is Selection Active Today?: " + isTodaySelected);
		    } else {
		        isTodaySelected = true; // Fallback default state
		        console.log("Fallback Default Executed: True today matches.");
		    }

		    // 4. Run conditional interface layout rules
		    if (status === 'DayNight') {
		        // Enforce time-of-day lock ONLY if the user has selected TODAY on the calendar
		        if (isTodaySelected) {
		            const currentHour = now.getHours();
		            // Block shift actions if it is between 7:00 AM and 6:00 PM today
		            if (currentHour >= 7 && currentHour < 18) {
		                alert("Night shift actions are only allowed after 06:00 PM.");
		                statusSelect.value = 'Present'; // Revert back to daytime presentation
		                
		                if (morningGroup) morningGroup.style.display = 'flex';
		                if (nightGroup) nightGroup.style.display = 'none';
		                return;
		            }
		        }

		        // Past selected dates (like yesterday the 24th) completely skip validation rules and unlock everything
		        if (morningGroup) morningGroup.style.display = 'none';
		        if (timeInBtn) timeInBtn.disabled = true;
		        if (timeOutBtn) timeOutBtn.disabled = true;

		        if (nightGroup) nightGroup.style.display = 'flex';
		        
		        if (nightInField) { nightInField.disabled = false; nightInField.removeAttribute('disabled'); }
		        if (nightOutField) { nightOutField.disabled = false; nightOutField.removeAttribute('disabled'); }
		        if (nightInBtn) { nightInBtn.disabled = false; nightInBtn.removeAttribute('disabled'); }
		        if (nightOutBtn) { nightOutBtn.disabled = false; nightOutBtn.removeAttribute('disabled'); }
		    } 
		    else if (status === 'Present') {
		        if (morningGroup) morningGroup.style.display = 'flex';
		        if (timeInBtn) { timeInBtn.disabled = false; timeInBtn.removeAttribute('disabled'); }
		        if (timeOutBtn) { timeOutBtn.disabled = false; timeOutBtn.removeAttribute('disabled'); }

		        if (nightGroup) nightGroup.style.display = 'none';
		        if (nightInBtn) nightInBtn.disabled = true;
		        if (nightOutBtn) nightOutBtn.disabled = true;
		    } 
		    else if (status === 'Absent' || status === 'Holiday') {
		        if (morningGroup) morningGroup.style.display = 'none';
		        if (nightGroup) nightGroup.style.display = 'none';
		        
		        if (timeInBtn) timeInBtn.disabled = true;
		        if (timeOutBtn) timeOutBtn.disabled = true;
		        if (nightInBtn) nightInBtn.disabled = true;
		        if (nightOutBtn) nightOutBtn.disabled = true;
		    }
		}

		// 5. Action Trigger: Timestamps values into target inputs on click events
		function setCurrentTime(inputId) {
		    const now = new Date();
		    const hours = String(now.getHours()).padStart(2, '0');
		    const minutes = String(now.getMinutes()).padStart(2, '0');
		    const timeString = `${hours}:${minutes}`;
		    
		    const inputField = document.getElementById(inputId);
		    if (inputField) {
		        inputField.value = timeString;
		    }

		    // 1. Handle Check-In Button Disabling States (Day or Night)
		    if (inputId === 'timeIn' || inputId === 'nightTimeIn') {
		        const targetBtn = document.getElementById(inputId === 'timeIn' ? 'timeInBtn' : 'btnNightIn');
		        if (targetBtn) targetBtn.disabled = true;
		    }
		    
		    // 2. FIXED: Handle Check-Out Button Disabling and Calculation States for Night Out
		    if (inputId === 'nightTimeOut') {
		        // Lock the Night Out button to prevent double-stamping entries
		        const targetBtn = document.getElementById('btnNightOut');
		        if (targetBtn) targetBtn.disabled = true;
		        
		        // Execute the dynamic shift-tier status evaluation calculator engine
		        calculateNightStatus(hours, minutes);
		    }
		    
		    // 3. FIXED: Trigger global session tracking duration counters to update state vectors
		    if (typeof calculateFormDuration === "function") {
		        calculateFormDuration();
		    }
		}

		// 6. Status Engine: Labels night configuration types dynamically based on out hours
		function calculateNightStatus(hours, minutes) {
		    const currentHour = parseInt(hours, 10);
		    const hiddenNightStatus = document.getElementById('nightStatusInput');
		    const selectElem = document.getElementById('statusSelect');
		    const nightOption = selectElem ? selectElem.querySelector('option[value="DayNight"]') : null;

		    let statusText = (currentHour >= 1 && currentHour <= 7) ? "Full Night" : 
		                     (currentHour === 23 || currentHour === 0) ? "Half Night" : "Short Night";

		    if (hiddenNightStatus) {
		        hiddenNightStatus.value = statusText; 
		    }
		    if (nightOption) {
		        nightOption.textContent = `Night (${statusText})`;
		    }
		}

		// 7. Event Listeners Initialization on document compilation
		document.addEventListener("DOMContentLoaded", function() {
		    // Initial evaluation run on load to look up current date value and print initial console logs
		    handleStatusChange();

		    const statusSelect = document.getElementById('statusSelect');
		    if (statusSelect) {
		        statusSelect.addEventListener('change', handleStatusChange);
		        
		        const dateInput = document.getElementById('todayDate');
		        if (dateInput) {
		            dateInput.addEventListener('change', handleStatusChange);
		        }
		        
		        const parentForm = statusSelect.closest('form');
		        if (parentForm) {
		            parentForm.addEventListener('submit', function() {
		                if (statusSelect.value === 'DayNight') {
		                    statusSelect.value = 'Present';
		                }
		            });
		        }
		    }
		});

		
		function showEditForm(event, id) {
		    // 1. Stop the page from opening a new tab or reloading
		    event.preventDefault(); 

		    // 2. Fetch data from your backend controller
		    fetch('/timesheets/edit/' + id)
		        .then(response => {
		            if (!response.ok) {
		                throw new Error('Network response was not ok');
		            }
		            return response.json();
		        })
		        .then(data => {
		            // 3. Fill the hidden ID input
		            document.querySelector('#editTimesheetForm input[name="id"]').value = data.id || '';
		            
		            // 4. Fill text and date inputs
		            document.querySelector('#editTimesheetForm input[name="date"]').value = data.date || '';
		            document.querySelector('#editTimesheetForm input[name="location"]').value = data.location || '';
		            
		            // 5. Fill Time inputs (expects HH:mm format)
		            document.querySelector('#editTimesheetForm input[name="timeIn"]').value = data.timeIn || '';
		            document.querySelector('#editTimesheetForm input[name="timeOut"]').value = data.timeOut || '';
		            document.querySelector('#editTimesheetForm input[name="nightTimeIn"]').value = data.nightTimeIn || '';
		            document.querySelector('#editTimesheetForm input[name="nightTimeOut"]').value = data.nightTimeOut || '';
		            
		            // 6. Set Dropdown options to select matching values
		            document.querySelector('#editTimesheetForm select[name="status"]').value = data.status || 'Present';
		            document.querySelector('#editTimesheetForm select[name="nightStatus"]').value = data.nightStatus || 'No Night Shift';

		            // 7. Update the submit action URL to point to the correct ID
		            document.getElementById('editTimesheetForm').action = '/timesheets/update/' + id;

		            // 8. Make the beautiful form pop up on screen
		            document.getElementById('editFormContainer').style.display = 'flex';
		        })
		        .catch(error => {
		            console.error('Error fetching entry details:', error);
		            alert('Could not load data. Please try again.');
		        });
		}

		function hideEditForm() {
		    // Safely hide the pop-up box
		    document.getElementById('editFormContainer').style.display = 'none';
		}
