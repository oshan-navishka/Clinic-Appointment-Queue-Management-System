/**
 * sidebar.js — Dynamic role-based sidebar builder
 * Called by all admin/super-admin pages to render correct nav.
 */
(function () {
    'use strict';

    var ADMIN_NAV = [
        { href: 'adminDashboard.html',  icon: 'fa-gauge-high',      label: 'Dashboard'     },
        { href: 'patients.html',        icon: 'fa-user-injured',    label: 'Patients'      },
        { href: 'appointments.html',    icon: 'fa-calendar-check',  label: 'Appointments'  },
        { href: 'medications.html',     icon: 'fa-pills',           label: 'Medications'   },
        { href: 'payments.html',        icon: 'fa-credit-card',     label: 'Payments'      },
        { href: 'invoices.html',        icon: 'fa-file-invoice',    label: 'Invoices'      },
        { href: 'ratings.html',         icon: 'fa-star',            label: 'Ratings'       },
        { href: 'myAccount.html',       icon: 'fa-key',             label: 'My Account'    }
    ];

    var SUPER_ADMIN_NAV = [
        { href: 'dashboard.html',           icon: 'fa-gauge-high',      label: 'Dashboard'          },
        { href: 'patients.html',            icon: 'fa-user-injured',    label: 'Patients'           },
        { href: 'doctors.html',             icon: 'fa-user-doctor',     label: 'Doctors'            },
        { href: 'specializations.html',     icon: 'fa-stethoscope',     label: 'Specializations'    },
        { href: 'clinicBranches.html',      icon: 'fa-hospital',        label: 'Branches'           },
        { href: 'appointments.html',        icon: 'fa-calendar-check',  label: 'Appointments'       },
        { href: 'medications.html',         icon: 'fa-pills',           label: 'Medications'        },
        { href: 'payments.html',            icon: 'fa-credit-card',     label: 'Payments'           },
        { href: 'invoices.html',            icon: 'fa-file-invoice',    label: 'Invoices'           },
        { href: 'ratings.html',             icon: 'fa-star',            label: 'Ratings'            },
        { href: 'doctorAvailability.html',  icon: 'fa-clock',           label: 'Availability &amp; Fees' },
        { href: 'userManagement.html',      icon: 'fa-users-gear',      label: 'User Management',
          style: 'background:rgba(239,68,68,.15);color:#fca5a5' },
        { href: 'myAccount.html',           icon: 'fa-key',             label: 'My Account'         }
    ];

    window.buildSidebar = function () {
        var sidebar = document.getElementById('sidebar');
        if (!sidebar) return;

        var role        = localStorage.getItem('userRole') || '';
        var currentPage = window.location.pathname.split('/').pop();
        var isSuperAdmin = (role === 'SUPER_ADMIN');
        var navItems     = isSuperAdmin ? SUPER_ADMIN_NAV : ADMIN_NAV;

        var logoArea = sidebar.querySelector('.logo-area');
        sidebar.innerHTML = '';
        if (logoArea) sidebar.appendChild(logoArea);

        navItems.forEach(function (item) {
            var a = document.createElement('a');
            a.href = item.href;
            a.innerHTML = '<i class="fas ' + item.icon + '"></i> ' + item.label;
            if (item.style) a.setAttribute('style', item.style);
            if (item.href === currentPage) a.classList.add('active');
            sidebar.appendChild(a);
        });

        // logout
        var logout = document.createElement('a');
        logout.href = '#';
        logout.setAttribute('onclick',
            'if(typeof logout==="function"){logout();}' +
            'else{localStorage.clear();window.location.href="login.html";}return false;');
        logout.setAttribute('style', 'margin-top:auto;border-top:1px solid rgba(255,255,255,.1);padding-top:1rem');
        logout.innerHTML = '<i class="fas fa-right-from-bracket"></i> Logout';
        sidebar.appendChild(logout);
    };

    document.addEventListener('DOMContentLoaded', function () {
        window.buildSidebar();
    });
})();
