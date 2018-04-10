import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Template } from '../template.model';

@Component({
    selector: 'jhi-template-list',
    templateUrl: './template-list.component.html',
    styleUrls: ['./template-list.component.scss']
})
export class TemplateListComponent implements OnInit {
    @Input() templates: Template[];
    @Output() select = new EventEmitter<Template>();

    constructor() { }

    ngOnInit() {
    }

    selectTemplate(template: Template) {
        this.select.emit(template);
    }
}
