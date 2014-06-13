<div>
    <label>{{name}}</label>
    <div class="checkbox" ng-repeat="option in options">
        <label>
            <input type="checkbox"
                   value="{{option}}"
                   ng-checked="selected.indexOf(option) > -1"
                   ng-click="toggleOption(option)"/> {{option}}
        </label>
    </div>
</div>