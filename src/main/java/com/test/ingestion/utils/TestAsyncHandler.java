package com.test.ingestion.utils;


public class TestAsyncHandler {

/*	public enum RequestType {
		Creation, Modification, Deletion
	}

	private String reqid = "";

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private SingleRequestDAO dataAccessService;

	//@Autowired
	//private org.springframework.scheduling.commonj.WorkManagerTaskExecutor workManagerTaskExecutor;

	public ResponseEntity<String> handleRequest(final RequestType requestType,
			final String request) {

		// ASYNC APPROACH - 1
		// Either use FutureResult
		
		 * Future<T> futureTask = workManagerTaskExecutor.submit(new Callable()
		 * {
		 * 
		 * @Override public Object call() throws Exception { // TODO
		 * Auto-generated method stub return null; } });
		 

		// ASYNC APPROACH - 2 -- Or Use WorkManager
		// Create instance of Work and WorkListener implementation
		Work work = new DelegatingWork(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(4000);
					try {

						switch (requestType) {

						case Creation:
							dataAccessService.insert(request);
							break;
						case Modification:
							dataAccessService.update(request);
							break;
						case Deletion:
							dataAccessService.delete(request);
							break;
						default:
							break;
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		// Schedule work for execution, which would start in parallel to current
		// thread
		WorkItem workItem = null;
		try {
			workItem = workManagerTaskExecutor.schedule(work,

			new WorkListener() {
				@Override
				public void workAccepted(WorkEvent arg0) {
					System.out.println("Accepted");
				}

				@Override
				public void workCompleted(WorkEvent arg0) {
					System.out.println("Completed");
				}

				@Override
				public void workRejected(WorkEvent arg0) {
					System.out.println("Rejected");
				}

				@Override
				public void workStarted(WorkEvent arg0) {
					System.out.println("Started");
				}
			});

		} catch (IllegalArgumentException e) {
			log.error(e);
			e.printStackTrace();
		} catch (WorkException e) {
			log.error(e);
			e.printStackTrace();
		}

		// make all work items synchronous :
		// workManagerTaskExecutor.waitForAll(workItems, timeout);
		// Or
		
		// futureTask.get(timeOut) // making blocking call

		
		 * if (workItem.getStatus() == WorkEvent.WORK_COMPLETED) { // }
		 

		// notifier.sendNotification(new PDINotification());

		return new ResponseEntity<String>(requestType + " In Progress....",
				HttpStatus.ACCEPTED);

		
		 * javax.xml.parsers.DocumentBuilderFactory factory =
		 * javax.xml.parsers.DocumentBuilderFactory.newInstance();
		 * javax.xml.parsers.DocumentBuilder db = null; try { db =
		 * factory.newDocumentBuilder(); } catch (ParserConfigurationException
		 * e1) { e1.printStackTrace(); } org.xml.sax.InputSource inStream = new
		 * org.xml.sax.InputSource();
		 * 
		 * inStream.setCharacterStream(new java.io.StringReader(request));
		 * org.w3c.dom.Document doc = null; try { doc = db.parse(inStream); }
		 * catch (SAXException e) { e.printStackTrace(); } catch (IOException e)
		 * { e.printStackTrace(); } //org.w3c.dom.NodeList nodeList =
		 * doc.getElementsByTagName("Account"); org.w3c.dom.NodeList nodeList =
		 * doc.getChildNodes(); // return new ResponseEntity<String>(request,
		 * HttpStatus.ACCEPTED);
		 

	}*/

}
