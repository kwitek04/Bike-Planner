import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  effect,
  inject,
  output,
  viewChild,
} from '@angular/core';
import * as L from 'leaflet';
import { LatLon } from '../models/geo';
import { MapStore } from './map-store';

const WROCLAW: L.LatLngTuple = [51.1079, 17.0385];

@Component({
  selector: 'app-map-shell',
  template: '<div #host class="map-host"></div>',
  styles: [':host, .map-host { display: block; width: 100%; height: 100%; }'],
})
export class MapShell implements AfterViewInit, OnDestroy {
  private readonly store = inject(MapStore);

  readonly mapClicked = output<LatLon>();
  readonly pointMoved = output<{ index: number; point: LatLon }>();

  private readonly host = viewChild.required<ElementRef<HTMLDivElement>>('host');
  private map?: L.Map;
  private readonly pointMarkers = L.layerGroup();
  private readonly routeLine = L.polyline([], { color: '#1d5fa6', weight: 5, opacity: 0.85 });

  constructor() {
    effect(() => this.renderPoints(this.store.routePoints()));
    effect(() => this.renderRoute(this.store.routeGeometry()));
  }

  ngAfterViewInit(): void {
    this.map = L.map(this.host().nativeElement).setView(WROCLAW, 12);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">autorzy OpenStreetMap</a>',
    }).addTo(this.map);

    this.routeLine.addTo(this.map);
    this.pointMarkers.addTo(this.map);
    this.map.on('click', (e) => this.mapClicked.emit({ lat: e.latlng.lat, lon: e.latlng.lng }));
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private renderPoints(points: LatLon[]): void {
    this.pointMarkers.clearLayers();
    points.forEach((p, index) => {
      const marker = L.marker([p.lat, p.lon], {
        draggable: true,
        icon: this.routePointIcon(index, points.length),
      });
      marker.on('dragend', () => {
        const { lat, lng } = marker.getLatLng();
        this.pointMoved.emit({ index, point: { lat, lon: lng } });
      });
      this.pointMarkers.addLayer(marker);
    });
  }

  private renderRoute(coordinates: number[][] | null): void {
    const latLngs = (coordinates ?? []).map(([lon, lat]) => L.latLng(lat, lon));
    this.routeLine.setLatLngs(latLngs);
    if (this.map && latLngs.length > 1) {
      this.map.fitBounds(this.routeLine.getBounds(), { padding: [40, 40] });
    }
  }

  private routePointIcon(index: number, count: number): L.DivIcon {
    const isStart = index === 0;
    const isEnd = count > 1 && index === count - 1;
    const kind = isStart ? 'start' : isEnd ? 'end' : 'via';
    const label = isStart ? 'A' : isEnd ? 'B' : String(index);
    return L.divIcon({
      className: `pin pin--${kind}`,
      html: `<span>${label}</span>`,
      iconSize: [24, 24],
      iconAnchor: [12, 12],
    });
  }
}
