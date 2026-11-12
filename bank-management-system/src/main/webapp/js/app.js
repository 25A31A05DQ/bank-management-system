(function () {
    'use strict';

    // Mobile sidebar toggle
    var toggle = document.getElementById('menuToggle');
    if (toggle) {
        toggle.addEventListener('click', function () {
            document.body.classList.toggle('nav-open');
        });
        document.addEventListener('click', function (event) {
            if (document.body.classList.contains('nav-open')
                && !event.target.closest('#sidebar') && event.target !== toggle) {
                document.body.classList.remove('nav-open');
            }
        });
    }

    // Success messages fade out after a few seconds (errors stay until the page changes)
    document.querySelectorAll('.alert-success').forEach(function (alertBox) {
        setTimeout(function () { alertBox.classList.add('hide'); }, 6000);
    });

    // Deposit / withdrawal form helpers (the server still validates everything)
    var form = document.getElementById('transactionForm');
    if (!form) { return; }

    var select = document.getElementById('customerId');
    var amount = document.getElementById('amount');
    var hint = document.getElementById('balanceHint');
    var currency = form.getAttribute('data-currency') || '';

    function selectedBalance() {
        var option = select.options[select.selectedIndex];
        var value = option ? parseFloat(option.getAttribute('data-balance')) : NaN;
        return isNaN(value) ? null : value;
    }

    function currentType() {
        var checked = form.querySelector('input[name="type"]:checked');
        return checked ? checked.value : 'DEPOSIT';
    }

    function format(value) {
        return currency + value.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    function refresh() {
        var balance = selectedBalance();
        var tooMuch = false;
        if (balance === null) {
            hint.textContent = 'Select an account to see its available balance.';
        } else {
            hint.textContent = 'Available balance: ' + format(balance);
            var value = parseFloat(amount.value);
            tooMuch = currentType() === 'WITHDRAWAL' && !isNaN(value) && value > balance;
        }
        hint.classList.toggle('warn', tooMuch);
        amount.setCustomValidity(tooMuch ? 'Amount is more than the available balance.' : '');
        if (tooMuch) {
            hint.textContent += ' - the amount is more than the available balance.';
        }
    }

    select.addEventListener('change', refresh);
    amount.addEventListener('input', refresh);
    form.querySelectorAll('input[name="type"]').forEach(function (radio) {
        radio.addEventListener('change', refresh);
    });
    refresh();
})();
