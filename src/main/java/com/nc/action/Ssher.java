package com.nc.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.utils.GlobalLogger;
import com.jcraft.jsch.*;

public class Ssher implements Action, Callable<Event> {
	
	private final String address;
	private final int port;
	private final long timeout;
	private final boolean keyAuth;
	private final String user;
	private final String auth;
	private final List<String> commands;
	private final ExecutorService es;
	private final AtomicBoolean interrupt = new AtomicBoolean(false);
	
	public Ssher(String address, int port, String user, boolean useKeyAuth, String auth,
			List<String> commands, long timeout) {
		super();
		this.address = address;
		this.port = port;
		this.user = user;
		this.auth = auth;
		this.keyAuth = useKeyAuth;
		this.commands = commands;
		this.timeout = timeout;
		if (timeout > 0) {
			this.es = Executors.newSingleThreadExecutor();
		} else {
			this.es = null;
		}
	}


	
	@Override
	public Event exec() {
		if (timeout > 0) {
			Future<Event> fe = es.submit(this);
			try {
				Event e = fe.get(timeout, TimeUnit.MILLISECONDS);
				es.shutdownNow();
				return e;
			} catch (TimeoutException e) {
				this.interrupt.set(true);
				fe.cancel(true);
				es.shutdownNow();
				return new Event(EventType.FAILURE, "timeout");
			} catch (InterruptedException e) {
				GlobalLogger.info(e.toString());
				return new Event(EventType.EXCEPTION, e.toString());
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		} else {
			return call();
		}
	}
	
	@Override
	public Event call() {
		try {
			SshResult sr = execSsh(user, auth, address,	port, commands, keyAuth);
			Ret lastCommand = sr.getCommandResults().get(
					sr.getCommandResults().size() - 1);
			if (sr.isSuccessful()) {
				return new Event(EventType.SUCCESS, lastCommand.getStdout().concat(lastCommand.getStderr()));
			} else{
				return new Event(EventType.FAILURE, address + ": ssh command '"
						+ lastCommand.getCommand() + "' failed: "
						+ lastCommand.getStderr().concat(lastCommand.getStdout()));
			}

		} catch (JSchException | IOException e) {
			return new Event(EventType.EXCEPTION, e.toString());
		}
	}
	
	
	private SshResult execSsh(String user, String auth, String host,
			int port, List<String> commands, boolean useKeyAuth) throws JSchException, IOException {		
		JSch jsch = new JSch();
		Session session = null;
		List<Ret> srl = new ArrayList<>();
		try {
			if (useKeyAuth) {
				jsch.addIdentity(auth);
				session = jsch.getSession(user, host, port);
			} else {
				session = jsch.getSession(user, host, port);
				session.setPassword(auth);
			}
			
			session.setConfig("StrictHostKeyChecking", "no");			
			session.connect();			
			
			batchExec(commands, srl, session);

			session.disconnect();
		} finally {
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}

		return new SshResult(srl);
	}
	
	
	private void batchExec(List<String> commands, List<Ret> results, Session session) throws JSchException, IOException{
		for (String com : commands) {
			Ret r = _exec(session, com);
			results.add(r);
			if(r.getRet() != 0){
				break;
			}
		}
	}
	
	private Ret _exec(Session session, String command) throws JSchException, IOException {
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		channel.setInputStream(null);
		OutputStream err = new ByteArrayOutputStream();
		((ChannelExec)channel).setErrStream(err);
		InputStream in = channel.getInputStream();
		channel.connect();

		byte[] tmp = new byte[1024];
		StringBuilder sb = new StringBuilder();
		while (interrupt.get() == false) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				sb.append(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				if (in.available() > 0)
					continue;
				return new Ret(command, channel.getExitStatus(), sb.toString(), err.toString());
			}
			try {
				Thread.sleep(500);
			} catch (Exception ee) {
			}
		}
		return new Ret(command, -1, sb.toString(), "timeout");
	}
	
	private static class Ret{
		final int ret;
		final String stdout;
		final String stderr;
		final String command;
		public Ret(String command, int ret, String stdout, String stderr) {
			super();
			this.command = command;
			this.ret = ret;
			this.stdout = stdout;
			this.stderr = stderr;
		}
		public String getCommand() {
			return command;
		}
		
		public int getRet() {
			return ret;
		}

		public String getStdout() {
			return stdout;
		}
		
		public String getStderr() {
			return stderr;
		}
		
		@Override
		public String toString(){
			return "command=" + command + ", ret=" + ret;
		}
	}
	
	private static class SshResult{
		private final List<Ret> commandResults;
		private final boolean isSuccessful;
		
		public SshResult(List<Ret> commandResults) {
			super();
			this.commandResults = commandResults;
			this.isSuccessful = commandResults.stream().noneMatch(r -> r.getRet() != 0);
		}
		public List<Ret> getCommandResults() {
			return commandResults;
		}
		public boolean isSuccessful() {
			return isSuccessful;
		}
		
	}

	
}
