package com.pzybrick.iote2e.ws.route;

import com.pzybrick.iote2e.schema.avro.Iote2eRequest;

public interface RouteIote2eRequest {
	public void routeToTarget( Iote2eRequest Iote2eRequest ) throws Exception;
}