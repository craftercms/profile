<div ng-repeat="definition in definitions | orderBy:predicate" ng-switch="definition.metadata.type">
    <div class="form-group" ng-switch-when="LARGE_TEXT">
        <label for="{{definition.name}}">{{definition.metadata.label}}</label>
        <textarea name="{{definition.name}}" class="form-control" ng-model="attributes[definition.name]"/>
    </div>
    <div class="form-group" ng-switch-when="NUMBER">
        <label for="{{definition.name}}">{{definition.metadata.label}}</label>
        <input name="{{definition.name}}" type="number" class="form-control" ng-model="attributes[definition.name]"/>
    </div>
    <div class="checkbox" ng-switch-when="BOOLEAN">
        <label>
            <input type="checkbox" ng-model="attributes[definition.name]"/> {{definition.metadata.label}}
        </label>
    </div>
    <div class="form-group" ng-switch-when="STRING_LIST">
        <editable-list name="{{definition.metadata.label}}" items="attributes[definition.name]"></editable-list>
    </div>
    <div class="form-group" ng-switch-when="COMPLEX">
        <label for="{{definition.name}}">{{definition.metadata.label}}</label>
        <pre>{{attributes[definition.name]|prettyStringify}}</pre>
    </div>
    <div class="form-group" ng-switch-default>
        <label for="{{definition.name}}">{{definition.metadata.label}}</label>
        <input name="{{definition.name}}" type="text" class="form-control" ng-model="attributes[definition.name]"/>
    </div>
</div>