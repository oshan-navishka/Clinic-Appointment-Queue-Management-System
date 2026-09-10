/**
 * emailjs-utils.js
 * Shared helper to send welcome credential emails via EmailJS.
 *
 * SETUP (one-time):
 * 1. Go to https://emailjs.com and create a free account
 * 2. Add a Gmail service → copy the Service ID
 * 3. Create an Email Template with these variables:
 *      {{to_email}}   {{to_name}}   {{username}}   {{password}}   {{role}}
 * 4. Copy your Template ID and Public Key (Account → API Keys)
 * 5. Replace the three constants below with your actual values
 */

var EMAILJS_SERVICE_ID  = 'YOUR_SERVICE_ID';   // e.g. 'service_abc123'
var EMAILJS_TEMPLATE_ID = 'YOUR_TEMPLATE_ID';  // e.g. 'template_xyz789'
var EMAILJS_PUBLIC_KEY  = 'YOUR_PUBLIC_KEY';   // e.g. 'abcDEFghiJKL...'

/**
 * sendWelcomeEmail — send credentials email via EmailJS
 * @param {string} toEmail   recipient email address
 * @param {string} toName    recipient full name
 * @param {string} username  login username
 * @param {string} password  plain-text password (before hashing)
 * @param {string} role      PATIENT / DOCTOR / ADMIN / SUPER_ADMIN
 * @param {function} onSuccess  optional callback on success
 * @param {function} onError    optional callback on error
 */
function sendWelcomeEmail(toEmail, toName, username, password, role, onSuccess, onError) {
    if (!toEmail || !toEmail.includes('@')) {
        console.warn('sendWelcomeEmail: invalid email, skipping');
        if (typeof onError === 'function') onError('Invalid email');
        return;
    }

    if (EMAILJS_SERVICE_ID === 'YOUR_SERVICE_ID') {
        console.warn('EmailJS not configured — skipping welcome email. Set EMAILJS_SERVICE_ID etc in emailjs-utils.js');
        return;
    }

    var roleLabels = {
        'PATIENT':     'Patient',
        'DOCTOR':      'Doctor',
        'ADMIN':       'Admin',
        'SUPER_ADMIN': 'Super Admin'
    };
    var roleLabel = roleLabels[role] || role;

    var templateParams = {
        to_email:  toEmail,
        to_name:   toName,
        username:  username,
        password:  password,
        role:      roleLabel,
        app_name:  'ClinicQ',
        login_url: window.location.origin + '/login.html'
    };

    emailjs.send(EMAILJS_SERVICE_ID, EMAILJS_TEMPLATE_ID, templateParams, EMAILJS_PUBLIC_KEY)
        .then(function (res) {
            console.log('Welcome email sent to', toEmail, res.status);
            if (typeof onSuccess === 'function') onSuccess(res);
        })
        .catch(function (err) {
            console.error('Failed to send welcome email:', err);
            if (typeof onError === 'function') onError(err);
        });
}
