var main = ( function() {

    var rate = null;
    var countries = null;
    
    return {
        
        submitForm: function() {
            
            console.log("submitForm init");
            
            if ( $("#value").val() === "" ) {
                alert("You must enter a number!  Please try again.");
                return false;
            }
            
            if ( $("#date").val() === "" ) {
                alert("You must specify a date!  Please try again.");
                return false;
            }
            
            $.ajax({

                url: 'CurrencyServlet',
                method: 'GET',
                data: $('#converter').serialize(),

                success: function(response) {
                    console.log("submitForm success");
                    rate = response;
                    main.createOutput();
                }
            });
            
        },
        
        createOutput: function() {
            console.log("Creating output");
            var thisInput = Number($("#value").val());
            var conversionCurrency = $("#currency_select").val();
            var conversionRate = Number(rate.rates[conversionCurrency]);
            console.log(conversionRate);
            console.log(thisInput);
            var newelement = document.createElement("p");
            var converted = thisInput * conversionRate;
            $(newelement).html("The equivalent value in "+ conversionCurrency+" is: " + converted.toFixed(2) + " on "+ rate["date"]);
            $("#output").append(newelement);
        },
        
        getCountries: function() {

            console.log("Get countries initiated");
            $.ajax({

                url: 'CountryServlet',
                method: 'GET',
                dataType: 'json',

                success: function(response) {
                    console.log("GetCountries success");
                    //countries = JSON.stringify(response);
                    //$("#output").html(countries);
                    countries = response;
                    main.createDropMenu();

                }

            });

            return false;

        },

        createDropMenu: function() {
            
            console.log("Creating drop menu");
            
            for(var key in countries) {
                
                if (key === "GBP") {
                    $("#currency_select").append('<option selected value='+key+'>'+key+'</option>');
                }
                else {
                    $("#currency_select").append('<option value='+key+'>'+key+'</option>');
                }
            }
            
        }
    
    };
    
}());


