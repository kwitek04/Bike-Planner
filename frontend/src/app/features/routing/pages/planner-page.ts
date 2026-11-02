import { Component, inject } from '@angular/core';
import { MapShell } from '../../../shared/map/map-shell';
import { PlannerPanel } from '../components/planner-panel';
import { RoutingFacade } from '../routing.facade';

@Component({
  selector: 'app-planner-page',
  imports: [PlannerPanel, MapShell],
  templateUrl: './planner-page.html',
  styleUrl: './planner-page.scss',
})
export class PlannerPage {
  private readonly facade = inject(RoutingFacade);

  protected onMapClicked(point: { lat: number; lon: number }): void {
    this.facade.addPoint(point);
  }

  protected onPointMoved(event: { index: number; point: { lat: number; lon: number } }): void {
    this.facade.movePoint(event.index, event.point);
  }
}
