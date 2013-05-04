(function( $ ) {
	var methods = {
		init : function( pOptions ) {
			return this.each(function() {
				var $this = $(this),
					options = $this.data('ugc');

				// If the plugin hasn't been initialized yet
				if ( ! options ) {
					/*
						Do more setup stuff here
					*/
					var settings = $.extend({
						'restUrl' : 'http://localhost:8080/social/rest',
						'resourceUrl' : 'http://localhost:8080/social/resources',
						'clientId' : 'c1',
						'outputType' : 'json',
						'target' : 'http://www.google.com',
						'userId' : null,
						'targetJQObj' : $this,
						'date-format' : 'ddd mmm dd yyyy HH:MM:ss', 
						'css-class' : 'ugc',
						/* { 'userId' : '', 'idType' : '', 'nickName' : '', 'firstName' : '', 'middleName' : '', 'lastName' : '', 'email' : '' } */
						'user' : null
					}, pOptions);

					$(this).data('ugc', settings);
					
					$this.append("Loading...");
					
					$.view().context({
						formattedDate: function( timestamp ) {
							return new Date(timestamp).format(settings.dateFormat);
						},
						anonymousIfNull: function( text ) {
							return (text)?text:"Anonymous";
						}
					});
					
					util.upsertUser(settings);

					$.get(settings.resourceUrl + '/templates/templates_1.0.html', function (data, textStatus, jqXHR) {
						var $data = $(data);
						
						$data.each(function (){
							if (this.id && this.type === "text/x-jquery-tmpl") {
								$.template(this.id, this.innerHTML);
							}
						});

						settings.templatesLoaded = true;
					});
				}
			});

		},
		
		destroy : function( ) {
			return this.each(function(){
				var $this = $(this),
					options = $this.data('ugc');
				
				// Namespacing FTW
				$(window).unbind('.ugc');
				$this.removeData('ugc');
				$this.empty();
				
				if (options && options.ellapseTimer) {
					window.clearInterval(options.ellapseTimer);
					options.ellapseTimer = null;
				}
			});
		},
		
		loadUGC : function ( ) {
			return this.each(function () {
				var $this = $(this),
					options = $this.data('ugc');

				if ( options ) {
					var url = options.restUrl + '/' + options.clientId + '/get/target.' + options.outputType; 
					var data = {'target' : options.target};
					
					$.ajax({
					    url: url,
					    data: data,
					    dataType : options.outputType,
					    cache: false,
					    type: 'GET',
					    success: function(aData, textStatus, jqXHR){
					    	$this.empty();
					    	util.updateEllapsedTimeText(aData.UGCList);
					    	util.renderUGC(aData, options, $this);
					    },
					    error: function(jqXHR, textStatus, errorThrown) {
					    	$.error('Could not load ugc: ' + textStatus + ' - ' + errorThrown);
					    }
					});
				}
			});
		}		
	};
	
	var second=1000;
	var minute=second*60;
	var hour=minute*60;
	var day=hour*24;
	var month=day*30;
	var year=day*365;
	
	var ellapseUpdateInterval = 5000;
	
	var util = {
		renderUGC : function (data, options, container) {
			if (options.templatesLoaded) {
				container.html($.render( data, 'ugcListTmpl' )).link(data);
				
				var $ugcDiv = $(' > div.ugc-list', container),
					$actionsDiv = $(' > div.actions ', container),
					$addUGCBtn = $('a', $actionsDiv);
				
				$addUGCBtn.click(function (event) {
					util.showTextUGCDialog('', options, $actionsDiv, $ugcDiv);
				});
				
				$('div.ugc', $ugcDiv).each(function () {
					util.wireUpUGC.apply( this, [ options ]);
				});
				
		    	util.scheduleTimeUpdates(options, data.UGCList);
			} else {
				setTimeout(function() {util.renderUGC(data, options, container);} , 200);
			}
		},
		
		wireUpUGC : function (options) {
			var $this = $(this),
				$actions = $('> div > div.user-ugc > div.footer > div.actions', $this),
				$like = $('> a.like', $actions),
				$reply = $('> a.reply', $actions),
				$flag = $('> a.flag', $actions);
			
			$like.click(function (event) {
				util.likeUGC($this.attr('ugc-id'), options, $this);
			});			

			$reply.click(function (event) {
				util.showTextUGCDialog($this.attr('ugc-id'), options, $this, $this);
			});			

			$flag.click(function (event) {
				util.flagUGC($this.attr('ugc-id'), options, $this);
			});
		},
		
		addTextUGC : function (body, parentId, options, appendTo) {
			if ( options ) {
				if (body != null && body.length > 0) {
					var url = options.restUrl + '/' + options.clientId + '/add/text/' + 
						options.userId + '/' + parentId + '.' + options.outputType;
					
					var data = {'target' : options.target, 'body' : body};
					
					$.ajax({
					    url: url,
					    data: data,
					    dataType : options.outputType,
					    cache: false,
					    type: 'POST',
					    success: function(aData, textStatus, jqXHR){
					    	if (aData.UGC) {
					    		util.updateEllapsedTimeText([aData.UGC]);
					    		util.observableAddUGC.apply(appendTo, [aData.UGC]);
					    		util.wireUpUGC.apply( $('#ugc_'+aData.UGC.id, appendTo), [options]);
					    	}
					    },
					    error: function(jqXHR, textStatus, errorThrown) {
					    	$.error('Could not add ugc: ' + textStatus + ' - ' + errorThrown);
					    }
					});
				}
			}
		},
		
		showTextUGCDialog : function (parentId, options, appendTo, appendUGCTo) {
			var data = { 'parentId' : parentId },
				html = $.render( data, 'addTextUGCTmpl' ),
				$d = $('<div>', {}).html(html),
				$addUGC = $('> div.add-ugc', $d),
				$body = $('> div > textarea', $addUGC);
			
			$('> div.actions > a.add-btn', $addUGC).click(function (event) {
				$d.remove();
				util.addTextUGC($body.val(), parentId, options,  appendUGCTo);
				return false;
			});
			
			$('> div.actions > a.cancel-btn', $addUGC).click(function (event) {
				$d.remove();
				return false;
			});
			
			appendTo.append($d);
		},
		
		likeUGC : function (ugcId, options, ugcDiv) {
			if ( options ) {
				var url = options.restUrl + '/' + options.clientId + '/like/' + options.userId + '/' + 
							ugcId + '.' + options.outputType; 
				
				$.ajax({
				    url: url,
				    dataType : options.outputType,
				    cache: false,
				    type: 'GET',
				    success: function(aData, textStatus, jqXHR){
				    	if (aData.UGC) {
				    		util.observableUpdateUGCProps.apply(ugcDiv, [aData.UGC]);
				    	}
				    },
				    error: function(jqXHR, textStatus, errorThrown) {
				    	$.error('Could not like ugc: ' + textStatus + ' - ' + errorThrown);
				    }
				});
			}
		},
		
		flagUGC : function (ugcId, options, ugcDiv) {
			if ( options ) {
				var url = options.restUrl + '/' + options.clientId + '/flag/' + options.userId + '/' + 
							ugcId + '.' + options.outputType; 
				
				$.ajax({
				    url: url,
				    dataType : options.outputType,
				    cache: false,
				    type: 'GET',
				    success: function(aData, textStatus, jqXHR){
				    	if (aData.UGC) {
				    		util.observableUpdateUGCProps.apply(ugcDiv, [aData.UGC]);
				    	}
				    },
				    error: function(jqXHR, textStatus, errorThrown) {
				    	$.error('Could not flag ugc: ' + textStatus + ' - ' + errorThrown);
				    }
				});
			}
		},
		
		upsertUser : function (options) {
			if ( options && options.user && !options.userId ) {
				var url = options.restUrl + '/' + options.clientId + '/upsert/user.' + options.outputType; 
				var data = options.user;
				
				$.ajax({
				    url: url,
				    data: data,
				    dataType : options.outputType,
				    cache: false,
				    type: 'GET',
				    success: function(aData, textStatus, jqXHR){
				    	if (aData.user) {
				    		options.user = aData.user;
				    		options.userId = aData.user.id;
				    	}
				    },
				    error: function(jqXHR, textStatus, errorThrown) {
				    	$.error('Could not upsert user: ' + textStatus + ' - ' + errorThrown + ' - user : ' + options.user);
				    }
				});
			}
		},		
		
		observableUpdateUGCProps : function (data) {
			var oOld = $.observable($.view((this.length)?this[0]:this).data);
			
			for (var key in data) {
				var value = data[key];
				if ($.isArray(value)) {
					// TODO: insert/remove array elements
				} else {
					oOld.setProperty(key, value);
				}
			}
		},
		
		observableAddUGC : function (data) {
			var old = $.view((this.length)?this[0]:this).data,
				children = $.observable(old.children?old.children:old.UGCList);
			children.insert( 0, data );
		},
		
		updateEllapsedTimeText : function (UGCList) {
			var now = new Date().getTime();
			
			for (var key in UGCList) {
				var ugc = UGCList[key];
				var millis = now - (new Date(ugc.dateAdded).getTime());
				
				var years = Math.floor(millis / year); 
				millis -= years * year;
				var months = Math.floor(millis / month);
				millis -= months * month;
				var days=Math.floor(millis / day);
				millis -= days * day;
				var hours = Math.floor(millis / hour);
				millis -= hours * hour;
				var mins = Math.floor(millis / minute);
				millis -= mins * minute;
				var secs = Math.floor(millis / second);
				
				var text = '';
				
				if      (years ) text = years  + ' year'  + ((years > 1)?'s':'');
				else if (months) text = months + ' month' + ((months> 1)?'s':'');
				else if (days  ) text = days   + ' day'   + ((days  > 1)?'s':'');
				else if (hours ) text = hours  + ' hour'  + ((hours > 1)?'s':'');
				else if (mins  ) text = mins   + ' minute'+ ((mins  > 1)?'s':'');
				else             text = secs   + ' second'+ ((secs  > 1)?'s':'');
				
				text = text + ' ago';
				
				$.observable(ugc).setProperty('ellapsedTime', text);
				
				if (ugc.children && ugc.children.length) {
					util.updateEllapsedTimeText(ugc.children);
				}
			}
		},
		
		scheduleTimeUpdates : function (options, data) {
			if (options.ellapseTimer) {
				window.clearInterval(options.ellapseTimer);
				options.ellapseTimer = null;
			}
			
			var t=window.setInterval(function () {util.updateEllapsedTimeText(data)}, ellapseUpdateInterval);
			options.ellapseTimer = t;
		}
	};

	$.fn.ugc = function(method) {
		// Method calling logic
		if ( methods[method] ) {
			return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
		} else if ( typeof method === 'object' || ! method ) {
			return methods.init.apply( this, arguments );
		} else {
			$.error( 'Method ' +  method + ' does not exist on jQuery.ugc' );
		}    
	};
})( jQuery );
