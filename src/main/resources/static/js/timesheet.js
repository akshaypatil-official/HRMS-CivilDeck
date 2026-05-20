window.onload = function() {
            document.getElementById('todayDate').valueAsDate = new Date();
            calculateHistory();
        };

        function setCurrentTime(fieldId) {
            const now = new Date();
            const timeVal = now.getHours().toString().padStart(2, '0') + ":" + now.getMinutes().toString().padStart(2, '0');
            document.getElementById(fieldId).value = timeVal;
            calculateFormDuration();
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
  
        
        // 2. Capture photo and overlay the current date & time
        function capturePhoto() {
            if (!video.srcObject) {
                alert("Please start the camera first.");
                return;
            }

            let ctx = canvas.getContext('2d');
            
            // Match canvas dimensions to the live video feed
            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;
            
            // Draw the current video frame onto the canvas
            ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
            
            // Generate the current date and time string
            let now = new Date();
            let timestamp = now.toLocaleString(); // Format: MM/DD/YYYY, HH:MM:SS AM/PM
            
            // Set text styling for the overlay
            let fontSize = Math.floor(canvas.height * 0.04); // Responsive font size (4% of image height)
            ctx.font = `bold ${fontSize}px Arial`;
            
            // Draw text shadow background for high visibility on any image
            ctx.fillStyle = "rgba(0, 0, 0, 0.6)";
            ctx.fillRect(15, canvas.height - fontSize - 25, ctx.measureText(timestamp).width + 20, fontSize + 15);
            
            // Draw the white timestamp text
            ctx.fillStyle = "#ffffff";
            ctx.textBaseline = "bottom";
            ctx.fillText(timestamp, 25, canvas.height - 20);
            
            // Convert canvas data to Base64 URL string
            let dataUrl = canvas.toDataURL('image/jpeg');
            
            // Update preview element and hidden input field for Thymeleaf form submission
            preview.src = dataUrl;
            preview.style.display = "block";
            hiddenInput.value = dataUrl;
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