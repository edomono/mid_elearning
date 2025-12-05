
document.addEventListener('DOMContentLoaded', function() {
    const participantDetailsModal = document.getElementById('participant-details-modal');
    const closeParticipantDetailsModalBtn = document.getElementById('close-participant-modal-btn');
    const editForm = document.getElementById('editParticipantForm');
    const viewAttendanceBtn = document.getElementById('view-attendance-btn');

    document.body.addEventListener('click', function(event) {
        if (event.target.classList.contains('view-participant-btn')) {
            event.preventDefault();
            const participantId = event.target.dataset.participantId;
            fetchParticipantDetails(participantId);
        }
    });

    if(closeParticipantDetailsModalBtn) {
        closeParticipantDetailsModalBtn.addEventListener('click', () => {
            participantDetailsModal.classList.add('hidden');
        });
    }

    window.addEventListener('click', (event) => {
        if (event.target === participantDetailsModal) {
            participantDetailsModal.classList.add('hidden');
        }
    });

    function fetchParticipantDetails(id) {
        fetch(`/api/admin/participants/${id}`) // Assuming an API endpoint for fetching participant details
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(participant => {
                console.log('Participant data received:', participant);
                const modalUsername = document.getElementById('modal-username');
                const modalEmail = document.getElementById('modal-email');
                const modalRole = document.getElementById('modal-role');

                console.log('modalUsername element:', modalUsername);
                console.log('modalEmail element:', modalEmail);
                console.log('modalRole element:', modalRole);

                if (modalUsername) modalUsername.value = participant.username;
                if (modalEmail) modalEmail.value = participant.email;
                if (modalRole) modalRole.value = participant.role;

                // Update form actions with participant ID
                if (editForm) editForm.action = `/admin/participant/${participant.id}/update`;

                const delForm = document.getElementById('deleteParticipantForm');
                if (delForm) delForm.action = `/admin/participant/${participant.id}/delete`;

                if (viewAttendanceBtn) {
                    // Show View Attendance only for users with role "USER"
                    if (participant.role && participant.role.toUpperCase() === 'USER') {
                        // Wire the modal-open behavior instead of full navigation
                        viewAttendanceBtn.href = `#`;
                        viewAttendanceBtn.style.display = 'inline-block';
                        viewAttendanceBtn.onclick = function(e) {
                            e.preventDefault();
                            // hide participant details modal before showing attendance modal
                            if (participantDetailsModal) participantDetailsModal.classList.add('hidden');
                            fetchAndShowAttendanceModal(participant.id, /* isAdmin= */ true);
                        };
                    } else {
                        // hide button for non-user roles
                        viewAttendanceBtn.style.display = 'none';
                    }
                }

                if (participantDetailsModal) {
                    participantDetailsModal.classList.remove('hidden');
                } else {
                    console.error('Error: participantDetailsModal element not found.');
                }
            })
            .catch(error => {
                console.error('Error fetching participant details:', error);
                alert('Failed to load participant details.');
            });
    }
});
